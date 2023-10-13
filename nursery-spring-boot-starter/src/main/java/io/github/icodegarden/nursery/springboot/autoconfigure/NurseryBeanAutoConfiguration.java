package io.github.icodegarden.nursery.springboot.autoconfigure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import io.github.icodegarden.nursery.springboot.SpringContext;
import io.github.icodegarden.nursery.springboot.lifecycle.GracefullyShutdownLifecycle;
import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyShutdown;
import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyStartup;
import io.github.icodegarden.nutrient.lang.lifecycle.RegistryGracefullyShutdown;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用的bean
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
@Slf4j
public class NurseryBeanAutoConfiguration {

	/**
	 * 如果用户有作为bean
	 */
	@Autowired(required = false)
	private List<io.github.icodegarden.nutrient.lang.registry.Registry> registrys;
	@Autowired(required = false)
	private List<GracefullyStartup> gracefullyStartups;
	@Autowired(required = false)
	private List<GracefullyShutdown> gracefullyShutdowns;

	@PostConstruct
	private void init() throws Throwable {
		/**
		 * 与springcloud的ServiceRegistry互不影响
		 */
		if (registrys != null) {
			for (io.github.icodegarden.nutrient.lang.registry.Registry registry : registrys) {
				GracefullyShutdown.Registry.singleton().register(new RegistryGracefullyShutdown(registry));// 默认下线优先级最高
			}
		}
		
		/**
		 * 无损上线
		 */
		if (!CollectionUtils.isEmpty(gracefullyStartups)) {
			for (GracefullyStartup gracefullyStartup : gracefullyStartups) {
				gracefullyStartup.start();
			}
		}

		/**
		 * 无损下线注册
		 */
		if (!CollectionUtils.isEmpty(gracefullyShutdowns)) {
			gracefullyShutdowns.forEach(gracefullyShutdown -> {
				GracefullyShutdown.Registry.singleton().register(gracefullyShutdown);
			});
		}
	}

	@Bean
	public SpringContext springContext() {
		log.info("nursery init bean of SpringContext");
		return new SpringContext();
	}

	/**
	 * 无损下线
	 */
	@ConditionalOnProperty(value = "icodegarden.nursery.bean.lifecycle.gracefullyShutdown.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public SmartLifecycle gracefullyShutdownLifecycle() {
		log.info("nursery init bean of GracefullyShutdownLifecycle");
		return new GracefullyShutdownLifecycle();
	}

}
