package io.github.icodegarden.nursery.servlet.web.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("commons-test-web")
public interface SelfFeign {

	@GetMapping("feign/at")
	public ResponseEntity<?> feignAT();
	
	@GetMapping("feign/tcc")
	public ResponseEntity<?> feignTCC();
}
