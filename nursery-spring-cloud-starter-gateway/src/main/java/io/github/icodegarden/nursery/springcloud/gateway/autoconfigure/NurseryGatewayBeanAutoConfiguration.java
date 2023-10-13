package io.github.icodegarden.nursery.springcloud.gateway.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springcloud.gateway.properties.NurseryGatewaySecurityProperties;
import io.github.icodegarden.nursery.springcloud.gateway.spi.AppProvider;
import io.github.icodegarden.nursery.springcloud.gateway.spi.JWTAuthenticationConverter;
import io.github.icodegarden.nursery.springcloud.gateway.spi.JWTTokenExtractor;
import io.github.icodegarden.nursery.springcloud.gateway.spi.OpenApiRequestValidator;
import io.github.icodegarden.nursery.springcloud.gateway.spi.impl.ConfiguredAppProvider;
import io.github.icodegarden.nursery.springcloud.gateway.spi.impl.DefaultJWTAuthenticationConverter;
import io.github.icodegarden.nursery.springcloud.gateway.spi.impl.DefaultJWTTokenExtractor;
import io.github.icodegarden.nursery.springcloud.gateway.spi.impl.DefaultOpenApiRequestValidator;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class NurseryGatewayBeanAutoConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public JWTTokenExtractor defaultJWTTokenExtractor() {
		return new DefaultJWTTokenExtractor();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public JWTAuthenticationConverter defaultJWTAuthenticationConverter() {
		return new DefaultJWTAuthenticationConverter();
	}

	@ConditionalOnMissingBean
	@Bean
	public AppProvider configuredAppProvider(NurseryGatewaySecurityProperties securityProperties) {
		return new ConfiguredAppProvider(securityProperties);
	}

	@ConditionalOnMissingBean
	@Bean
	public OpenApiRequestValidator defaultOpenApiRequestValidator() {
		return new DefaultOpenApiRequestValidator();
	}
}
