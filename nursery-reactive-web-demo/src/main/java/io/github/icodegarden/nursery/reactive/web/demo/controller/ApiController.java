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

	@GetMapping("/anonymous/v1/infos")
	public Mono<ResponseEntity<Object>> info(ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok("ok"));
	}
}
