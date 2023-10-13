package io.github.icodegarden.nursery.springcloud.gateway.core.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;

import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveApiResponseAuthenticationEntryPoint;
import io.github.icodegarden.nursery.springboot.web.reactive.security.ReactiveApiResponseAuthenticationEntryPoint.ApiResponseBuilder;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.signature.App;
import io.github.icodegarden.nursery.springcloud.gateway.util.NurseryGatewayUtils;
import io.github.icodegarden.nutrient.lang.spec.response.ApiResponse;
import io.github.icodegarden.nutrient.lang.spec.response.ErrorCodeException;
import io.github.icodegarden.nutrient.lang.spec.response.InternalApiResponse;
import io.github.icodegarden.nutrient.lang.spec.response.OpenApiResponse;
import io.github.icodegarden.nutrient.lang.spec.sign.OpenApiRequestBody;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class ApiResponseServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

	private final ServerAuthenticationEntryPoint authenticationEntryPoint;

	public ApiResponseServerAuthenticationFailureHandler() {
		this.authenticationEntryPoint = new ReactiveApiResponseAuthenticationEntryPoint(new OpenapiResponseSignBuilder());
	}

	@Override
	public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
		return authenticationEntryPoint.commence(webFilterExchange.getExchange(), exception);
	}

	private class OpenapiResponseSignBuilder implements ApiResponseBuilder {

		@Override
		public ApiResponse build(ServerWebExchange exchange, ErrorCodeException ece) {
			OpenApiRequestBody requestBody = NurseryGatewayUtils.getOpenApiRequestBody(exchange);
			if (requestBody != null) {
				OpenApiResponse openApiResponse = OpenApiResponse.fail(requestBody.getMethod(), ece);

				/**
				 * response签名
				 */
				App app = NurseryGatewayUtils.getApp(exchange);
				if (app != null) {
					if (log.isInfoEnabled()) {
						log.info("Authentication Failure, app_name:{}, request_id:{}, {}", app.getAppName(),
								requestBody.getRequest_id(), ece.getMessage());
					}

					String sign = NurseryGatewayUtils.responseSign(openApiResponse, requestBody.getSign_type(), app);
					openApiResponse.setSign(sign);
				}
				return openApiResponse;
			} else {
				return InternalApiResponse.fail(ece);
			}
		}
	}
}