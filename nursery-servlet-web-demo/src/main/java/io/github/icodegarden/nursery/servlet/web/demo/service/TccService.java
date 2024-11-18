package io.github.icodegarden.nursery.servlet.web.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.seata.core.context.RootContext;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextParameter;
import org.apache.seata.rm.tcc.api.LocalTCC;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.icodegarden.nursery.servlet.web.demo.feign.SelfFeign;
import io.github.icodegarden.nursery.servlet.web.demo.mapper.ConsumerSystemMapper;
import io.github.icodegarden.nursery.servlet.web.demo.pojo.persistence.ConsumerSystemPO;
import io.github.icodegarden.nursery.servlet.web.demo.pojo.transfer.TccDTO;
import io.github.icodegarden.nursery.springcloud.seata.util.SeataBizContextUtils;
import io.github.icodegarden.nutrient.lang.util.SystemUtils;
import net.sf.jsqlparser.statement.alter.AlterSystemOperation;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@LocalTCC // @GlobalTransactional在本地，且本地也需要事务，则需要加@LocalTCC（可以在java interface类上）
@Service
public class TccService {
	@Autowired
	private ConsumerSystemMapper consumerSystemMapper;
	@Autowired
	private SelfFeign selfFeign;

	@TwoPhaseBusinessAction(name = "tcc1", commitMethod = "tcc1commit", rollbackMethod = "tcc1rollback", useTCCFence = false) // 可以在接口上
//		@Transactional
	public ConsumerSystemPO tcc1(@BusinessActionContextParameter(paramName = "dto") TccDTO dto) throws Exception {
		selfFeign.feignTCC();

		System.out.println("request seata TCC, xid:" + RootContext.getXID());

		ConsumerSystemPO po = new ConsumerSystemPO();
		po.setId(dto.getId());
		po.setName(dto.getName());
		po.setAppId(System.currentTimeMillis() + "");
		po.setEmail("e");
		po.setSaslPassword("aaa");
		po.setSaslUsername("aaa");

		po.setCreatedBy("xff");
		po.setCreatedAt(SystemUtils.now());
		po.setUpdatedBy("xff");
		po.setUpdatedAt(po.getUpdatedAt());
		consumerSystemMapper.add(po);
		
		HashMap ext1 = new HashMap();
		ext1.put("a", 1);
		ext1.put("b", "mystring");
		SeataBizContextUtils.setValue("jsonObject", ext1);
		
		ArrayList<Object> arrayList = new ArrayList<>();
		arrayList.add("one");
		arrayList.add("two");
		SeataBizContextUtils.setValue("jsonArray", arrayList);
		
		SeataBizContextUtils.setValue("long", 100L);

//			Thread.sleep(10000);

		int i = 1 / 0;
		return po;
	}

	public void tcc1commit(BusinessActionContext context) {
		System.out.println("tcc1commit");

		Map<String, Object> actionContext = context.getActionContext();
	}

	public void tcc1rollback(BusinessActionContext context) {
		System.out.println("tcc1rollback");

		System.out.println(context);
		Map<String, Object> actionContext = context.getActionContext();

		Object l = SeataBizContextUtils.getValue("long");
		Map jsonObject = SeataBizContextUtils.getValueJsonfy("jsonObject", Map.class);
		List<String> jsonArray = SeataBizContextUtils.getValueJsonfyArray("jsonArray", String.class);
		
		Map dto = (Map)SeataBizContextUtils.getValue("dto");
		consumerSystemMapper.delete(dto.get("id"));
	}

	@TwoPhaseBusinessAction(name = "tcc2", commitMethod = "tcc2commit", rollbackMethod = "tcc2rollback", useTCCFence = false)
//		@Transactional
	public ConsumerSystemPO tcc2(@BusinessActionContextParameter(paramName = "dto") TccDTO dto) throws Exception {
		System.out.println("request feign TCC, xid:" + RootContext.getXID());

		ConsumerSystemPO po = new ConsumerSystemPO();
		po.setId(dto.getId());
		po.setName(dto.getName());
		po.setAppId(System.currentTimeMillis() + "");
		po.setEmail("e");
		po.setSaslPassword("aaa");
		po.setSaslUsername("aaa");

		po.setCreatedBy("xff");
		po.setCreatedAt(SystemUtils.now());
		po.setUpdatedBy("xff");
		po.setUpdatedAt(po.getUpdatedAt());
		consumerSystemMapper.add(po);
		
		HashMap ext1 = new HashMap();
		ext1.put("a", 11);
		ext1.put("b", "mystring222");
		SeataBizContextUtils.setValue("jsonObject", ext1);

		return po;
	}

	public void tcc2commit(BusinessActionContext context) {
		System.out.println("tcc2commit");

		Map<String, Object> actionContext = context.getActionContext();
	}

	public void tcc2rollback(BusinessActionContext context) {
		System.out.println("tcc2rollback");

		System.out.println(context);
		Map<String, Object> actionContext = context.getActionContext();
		
		Map dto = (Map)actionContext.get("dto");
		consumerSystemMapper.delete(dto.get("id"));
	}
}