package io.github.icodegarden.nursery.reactive.web.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@RestController
public class ApiController {
	int i=0;

	@GetMapping("/anonymous/v1/infos")
	public Mono<ResponseEntity<Object>> info(ServerWebExchange exchange) {
//		System.out.println(Thread.currentThread().getName());
//		System.out.println(++i);
//		try {
//			Thread.sleep(99999999);
//		} catch (InterruptedException e) {
//		}
		return Mono.just(ResponseEntity.ok("ok"));
	}
}
