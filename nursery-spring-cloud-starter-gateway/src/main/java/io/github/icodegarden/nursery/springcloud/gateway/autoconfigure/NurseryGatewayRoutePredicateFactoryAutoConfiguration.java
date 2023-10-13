package io.github.icodegarden.nursery.springcloud.gateway.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springcloud.gateway.predicate.BodyMethodRoutePredicateFactory;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class NurseryGatewayRoutePredicateFactoryAutoConfiguration {

	@Bean
	public BodyMethodRoutePredicateFactory bodyMethodRoutePredicateFactory() {
		return new BodyMethodRoutePredicateFactory();
	}

}
