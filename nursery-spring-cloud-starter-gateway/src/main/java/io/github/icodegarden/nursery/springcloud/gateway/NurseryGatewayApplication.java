package io.github.icodegarden.nursery.springcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import io.github.icodegarden.nursery.springboot.web.reactive.util.ReactiveWebUtils;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@SpringBootApplication
public class NurseryGatewayApplication {

	/**
	 * 适用于项目自身不需要被spring扫描
	 */
	public static void main(String[] args) throws Exception {
		initServerConfig(args);

		SpringApplication.run(NurseryGatewayApplication.class, args);
	}

	/**
	 * 适用于项目自身需要被spring扫描
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
		initServerConfig(args);

		return SpringApplication.run(new Class[] { NurseryGatewayApplication.class, primarySource }, args);
	}

	public static void initServerConfig(String[] args) {
		ReactiveWebUtils.initGatewayServerConfig();
	}
}
