package io.github.icodegarden.nursery.springcloud.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springcloud.lifecycle.ServiceRegistryGracefullyShutdown;
import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyShutdown;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
@Slf4j
public class NurseryCloudBeanAutoConfiguration {

	/**
	 * 公共的 可能不是springcloud项目
	 */
	@ConditionalOnClass({ ServiceRegistry.class })
	@Configuration
	protected static class ServiceRegistryGracefullyShutdownAutoConfiguration {
		@Autowired(required = false)
		private ServiceRegistry serviceRegistry;
		@Autowired(required = false)
		private Registration registration;

		@PostConstruct
		private void init() {
			log.info("nursery init bean of ServiceRegistryGracefullyShutdownAutoConfiguration");
			if (serviceRegistry != null && registration != null) {
				GracefullyShutdown.Registry.singleton()
						.register(new ServiceRegistryGracefullyShutdown(serviceRegistry, registration));// 默认下线优先级最高
			}
		}
	}
}
