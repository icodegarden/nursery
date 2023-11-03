package io.github.icodegarden.commons.test.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@RestController
public class ApiController {

	@GetMapping("/anonymous/v1/infos")
	public Mono<ResponseEntity<Object>> info() {
		return Mono.just(ResponseEntity.ok("ok"));
	}
}
