package io.github.icodegarden.nursery.springboot.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springboot.endpoint.ReadinessEndpoint;
import io.github.icodegarden.nursery.springboot.endpoint.ReadinessEndpointWebExtension;
import io.github.icodegarden.nursery.springboot.endpoint.ReadinessHealthIndicator;
import io.github.icodegarden.nursery.springboot.properties.NurseryEndpointProperties;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableConfigurationProperties(NurseryEndpointProperties.class)
//@Conditional(ReadinessEndpointEnabledConditions.class)//只针对网关
/**
 * 默认不开，网关配置打开
 */
@ConditionalOnProperty(value = "icodegarden.nursery.endpoint.readiness.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnClass(HealthIndicator.class)//要求有actuator
@Configuration
public class NurseryReadinessEndpointAutoConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public ReadinessEndpoint readinessEndpoint() {
		ReadinessEndpoint readinessEndpoint = new ReadinessEndpoint();

		return readinessEndpoint;
	}

	@Bean
	@ConditionalOnBean(ReadinessEndpoint.class)
	@ConditionalOnMissingBean
	public ReadinessEndpointWebExtension readinessEndpointWebExtension(ReadinessEndpoint readinessEndpoint) {
		return new ReadinessEndpointWebExtension(readinessEndpoint);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnEnabledHealthIndicator("readiness")
	public ReadinessHealthIndicator readinessHealthIndicator(ReadinessEndpoint readinessEndpoint) {
		return new ReadinessHealthIndicator(readinessEndpoint);
	}
}
