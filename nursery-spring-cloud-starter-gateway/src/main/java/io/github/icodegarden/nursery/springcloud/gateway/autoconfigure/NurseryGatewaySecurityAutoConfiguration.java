package io.github.icodegarden.nursery.springcloud.gateway.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveApiResponseAccessDeniedHandler;
import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveApiResponseAuthenticationEntryPoint;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.ApiResponseServerAuthenticationFailureHandler;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.AppServerAuthenticationSuccessHandler;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.AuthMatcher;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.NoOpReactiveAuthenticationManager;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.UserServerAuthenticationSuccessHandler;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.jwt.JWTAuthenticationWebFilter;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.jwt.JWTServerAuthenticationConverter;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.signature.SignatureAuthenticationConfig;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.signature.SignatureAuthenticationWebFilter;
import io.github.icodegarden.nursery.springcloud.gateway.properties.NurseryGatewaySecurityProperties;
import io.github.icodegarden.nursery.springcloud.gateway.properties.NurseryGatewaySecurityProperties.Jwt;
import io.github.icodegarden.nursery.springcloud.gateway.spi.AppProvider;
import io.github.icodegarden.nursery.springcloud.gateway.spi.AuthWebFilter;
import io.github.icodegarden.nursery.springcloud.gateway.spi.AuthorizeExchangeSpecConfigurer;
import io.github.icodegarden.nursery.springcloud.gateway.spi.JWTAuthenticationConverter;
import io.github.icodegarden.nursery.springcloud.gateway.spi.JWTTokenExtractor;
import io.github.icodegarden.nursery.springcloud.gateway.spi.OpenApiRequestValidator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author Fangfang.Xu
 */
@ConditionalOnProperty(value = "icodegarden.nursery.gateway.security.support.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(NurseryGatewaySecurityProperties.class)
@Configuration
@EnableWebFluxSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class NurseryGatewaySecurityAutoConfiguration {

	@Autowired
	private NurseryGatewaySecurityProperties securityProperties;
	@Autowired
	private ServerCodecConfigurer codecConfigurer;
	@Autowired(required = false)
	private AuthorizeExchangeSpecConfigurer authorizeExchangeSpecConfigurer;
	@Autowired
	private AppProvider appProvider;
	@Autowired
	private OpenApiRequestValidator openApiRequestValidator;
	@Autowired(required = false)
	private AuthWebFilter authWebFilter;

	@Autowired
	private JWTTokenExtractor jwtTokenExtractor;
	@Autowired
	private JWTAuthenticationConverter jwtAuthenticationConverter;
	@Autowired(required = false)
	private ReactiveAuthenticationManager authenticationManager;
	@Autowired(required = false)
	private ServerAuthenticationConverter serverAuthenticationConverter;
	@Autowired(required = false)
	private ServerAuthenticationSuccessHandler serverAuthenticationSuccessHandler;
	@Autowired(required = false)
	private ServerAuthenticationFailureHandler authenticationFailureHandler;

	@Bean
	public AuthMatcher authMatcher() {
		return new AuthMatcher(securityProperties);
	}

	/**
	 * 配置方式要换成 WebFlux的方式
	 */
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
		ReactiveApiResponseAuthenticationEntryPoint serverAuthenticationEntryPoint = new ReactiveApiResponseAuthenticationEntryPoint();

		AuthorizeExchangeSpec authorizeExchangeSpec = httpSecurity//
				.exceptionHandling()//
				.authenticationEntryPoint(serverAuthenticationEntryPoint)//
				.accessDeniedHandler(new ReactiveApiResponseAccessDeniedHandler())//
				.and()//
				.csrf()//
				.disable()//
				.headers()//
				.frameOptions()//
				.disable()//
				.and()//
				.authorizeExchange();

		if (authorizeExchangeSpecConfigurer != null) {
			log.info("gateway security AuthorizeExchangeSpecConfigurer is exist, do config custom");
			authorizeExchangeSpecConfigurer.config(securityProperties, authorizeExchangeSpec);
		} else {
			log.info("gateway security AuthorizeExchangeSpecConfigurer not exist, do config default");
			AuthorizeExchangeSpecConfigurer.configDefault(securityProperties, authorizeExchangeSpec);
		}

		WebFilter webFilter;
		if (authWebFilter != null) {
			/**
			 * 自定义的优先
			 */
			webFilter = authWebFilter;
		} else if (securityProperties.getJwt() != null) {
			Jwt jwt = securityProperties.getJwt();
			log.info("gateway security config Authentication WebFilter by jwt:{}", jwt);

			webFilter = new JWTAuthenticationWebFilter(authMatcher(),
					authenticationManager != null ? authenticationManager : new NoOpReactiveAuthenticationManager(),
					serverAuthenticationConverter != null ? serverAuthenticationConverter
							: new JWTServerAuthenticationConverter(jwt.getSecretKey(), jwtTokenExtractor,
									jwtAuthenticationConverter),
					serverAuthenticationSuccessHandler != null ? serverAuthenticationSuccessHandler
							: new UserServerAuthenticationSuccessHandler(),
					authenticationFailureHandler != null ? authenticationFailureHandler
							: new ApiResponseServerAuthenticationFailureHandler());
		} else if (securityProperties.getSignature() != null) {
			NurseryGatewaySecurityProperties.Signature signature = securityProperties.getSignature();
			log.info("gateway security config Authentication WebFilter by signature:{}", signature);
			SignatureAuthenticationConfig config = new SignatureAuthenticationConfig(codecConfigurer, appProvider,
					openApiRequestValidator,
					authenticationManager != null ? authenticationManager : new NoOpReactiveAuthenticationManager(),
					serverAuthenticationSuccessHandler != null ? serverAuthenticationSuccessHandler
							: new AppServerAuthenticationSuccessHandler(appProvider, signature.getHeaderAppKey()),
					authenticationFailureHandler != null ? authenticationFailureHandler
							: new ApiResponseServerAuthenticationFailureHandler());
			webFilter = new SignatureAuthenticationWebFilter(authMatcher(), config);
		} else {
			log.info("gateway security config Authentication WebFilter by NoOp");
			webFilter = new NoOpWebFilter();
		}

		authorizeExchangeSpec//
				.and()//
				.addFilterBefore(webFilter, SecurityWebFiltersOrder.AUTHORIZATION);

		return httpSecurity.build();
	}

	private class NoOpWebFilter implements AuthWebFilter {
		@Override
		public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
			return chain.filter(exchange);
		}

	}
}
