package io.github.icodegarden.nursery.servlet.web.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import io.github.icodegarden.nursery.springboot.SpringContext;
import io.github.icodegarden.nutrient.hbase.HBaseEnv;
import io.github.icodegarden.nutrient.kafka.reliability.ReliabilityProducer;
import io.github.icodegarden.nutrient.redis.RedisExecutor;
import io.github.icodegarden.nutrient.zookeeper.ZooKeeperHolder;
import io.github.icodegarden.wing.Cacher;

/**
 * 检验xxxAutoConfiguration是否都正常工作
 * @author Fangfang.Xu
 *
 */
@RestController
public class AutowiredValidateController {
	
	@Autowired
	SpringContext springContext;
	@Autowired
	RedisExecutor redisExecutor;
	@Autowired
	Cacher cacher;
	@Autowired
	ElasticsearchClient elasticsearchClient;
	@Autowired
	HBaseEnv hbaseEnv;
	@Autowired
	ReliabilityProducer reliabilityProducer;
	@Autowired
	ZooKeeperHolder zooKeeperHolder;

//	@GetMapping("api/v1/caches")
//	public ResponseEntity<?> createUser() {
//		String str = cacher.getElseSupplier("k1", () -> "v1", 3);
//		return ResponseEntity.ok(str);
//	}

}
