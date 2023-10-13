package io.github.icodegarden.nursery.servlet.web.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableFeignClients("io.github.icodegarden.nursery.servlet.web.demo.feign")
@SpringBootApplication
public class NurseryServletWebDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NurseryServletWebDemoApplication.class, args);
	}

}