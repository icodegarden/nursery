package io.github.icodegarden.nursery.servlet.web.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.icodegarden.nursery.servlet.web.demo.pojo.persistence.ConsumerSystemPO;
import io.github.icodegarden.nursery.servlet.web.demo.service.TccService;
import io.seata.spring.annotation.GlobalTransactional;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@RestController
public class SeataTccController {

	@Autowired
	private TccService tccService;

	@GlobalTransactional//TCC模式时必须放在@LocalTCC Service的外面才起作用
	@GetMapping("seata/tcc")
	public ResponseEntity<?> tcc() throws Exception {
		ConsumerSystemPO po = tccService.tcc1(System.currentTimeMillis(), "1-"+System.currentTimeMillis());

		return ResponseEntity.ok(po);
	}

	@GetMapping("feign/tcc")
	public ResponseEntity<?> feignTCC() throws Exception {
		ConsumerSystemPO po = tccService.tcc2(System.currentTimeMillis(), "2-"+System.currentTimeMillis());
		
		return ResponseEntity.ok(po);
	}
	
	

}
