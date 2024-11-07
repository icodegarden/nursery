package io.github.icodegarden.nursery.servlet.web.demo.controller;

import org.apache.seata.core.context.RootContext;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.icodegarden.nursery.servlet.web.demo.feign.SelfFeign;
import io.github.icodegarden.nursery.servlet.web.demo.mapper.ConsumerSystemMapper;
import io.github.icodegarden.nursery.servlet.web.demo.pojo.persistence.ConsumerSystemPO;
import io.github.icodegarden.nutrient.lang.util.SystemUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@RestController
public class SeataATController {

	@Autowired
	private ConsumerSystemMapper consumerSystemMapper;
	@Autowired
	private SelfFeign selfFeign;

	@GlobalTransactional//AT事务
	@Transactional//@Transactional只回滚本地事务
	@GetMapping("seata/at")
	public ResponseEntity<?> seataAT() throws Exception {
		selfFeign.feignAT();

		System.out.println("request seata AT, xid:"+RootContext.getXID());

		ConsumerSystemPO po = new ConsumerSystemPO();
		po.setId(System.currentTimeMillis());
		po.setName("1-" + System.currentTimeMillis() + "");
		po.setAppId(System.currentTimeMillis() + "");
		po.setEmail("e");
		po.setSaslPassword("aaa");
		po.setSaslUsername("aaa");

		po.setCreatedBy("xff");
		po.setCreatedAt(SystemUtils.now());
		po.setUpdatedBy("xff");
		po.setUpdatedAt(po.getUpdatedAt());
		consumerSystemMapper.add(po);
		
//		Thread.sleep(10000);
		
		int i=1/0;

		return ResponseEntity.ok(po);
	}

	@Transactional
	@GetMapping("feign/at")
	public ResponseEntity<?> feignAT() {
		System.out.println("request feign AT, xid:"+RootContext.getXID());

		ConsumerSystemPO po = new ConsumerSystemPO();
		po.setId(System.currentTimeMillis());
		po.setName("2-" + System.currentTimeMillis() + "");
		po.setAppId(System.currentTimeMillis() + "");
		po.setEmail("e");
		po.setSaslPassword("aaa");
		po.setSaslUsername("aaa");

		po.setCreatedBy("xff");
		po.setCreatedAt(SystemUtils.now());
		po.setUpdatedBy("xff");
		po.setUpdatedAt(po.getUpdatedAt());
		consumerSystemMapper.add(po);

		return ResponseEntity.ok(po);
	}
}
