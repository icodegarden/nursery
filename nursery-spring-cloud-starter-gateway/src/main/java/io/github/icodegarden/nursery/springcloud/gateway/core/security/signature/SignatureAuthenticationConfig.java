package io.github.icodegarden.nursery.springcloud.gateway.core.security.signature;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;

import io.github.icodegarden.nursery.springcloud.gateway.spi.AppProvider;
import io.github.icodegarden.nursery.springcloud.gateway.spi.OpenApiRequestValidator;
import lombok.Getter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Getter
@ToString
public class SignatureAuthenticationConfig {
	private ServerCodecConfigurer codecConfigurer;
//		private Set<String> acceptPathPatterns;
	private AppProvider appProvider;
	private OpenApiRequestValidator openApiRequestValidator;
	private ReactiveAuthenticationManager authenticationManager;
	private ServerAuthenticationSuccessHandler authenticationSuccessHandler;
	private ServerAuthenticationFailureHandler authenticationFailureHandler;

	public SignatureAuthenticationConfig(ServerCodecConfigurer codecConfigurer, AppProvider appProvider,
			OpenApiRequestValidator openApiRequestValidator, ReactiveAuthenticationManager authenticationManager,
			ServerAuthenticationSuccessHandler authenticationSuccessHandler,
			ServerAuthenticationFailureHandler authenticationFailureHandler) {
		this.codecConfigurer = codecConfigurer;
		this.appProvider = appProvider;
		this.openApiRequestValidator = openApiRequestValidator;
		this.authenticationManager = authenticationManager;
		this.authenticationSuccessHandler = authenticationSuccessHandler;
		this.authenticationFailureHandler = authenticationFailureHandler;
	}
}