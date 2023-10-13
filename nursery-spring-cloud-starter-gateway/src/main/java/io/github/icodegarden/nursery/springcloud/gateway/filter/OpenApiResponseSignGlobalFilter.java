package io.github.icodegarden.nursery.springcloud.gateway.filter;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyDecoder;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyEncoder;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory.Config;
import org.springframework.core.Ordered;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.github.icodegarden.nursery.springcloud.gateway.core.security.AuthMatcher;
import io.github.icodegarden.nursery.springcloud.gateway.core.security.signature.App;
import io.github.icodegarden.nursery.springcloud.gateway.properties.NurseryGatewaySecurityProperties;
import io.github.icodegarden.nursery.springcloud.gateway.spi.AppProvider;
import io.github.icodegarden.nursery.springcloud.gateway.util.NurseryGatewayUtils;
import io.github.icodegarden.nutrient.lang.spec.response.OpenApiResponse;
import io.github.icodegarden.nutrient.lang.spec.sign.OpenApiRequestBody;
import io.github.icodegarden.nutrient.lang.util.JsonUtils;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * <h1>对OpenApiResponse设置签名</h1>
 * 只对认证成功的response起作用（包括调用失败、业务失败等），认证失败的则是由ServerAuthenticationFailureHandler的实现类处理的<br>
 * 
 * 这个filter的order顺序是最前<br>
 * 
 * 这个类的生效条件是openapi模式<br>
 * 
 * 所有GlobalFilter的顺序在认证的WebFilter（AppKeyAuthenticationWebFilter、JWTAuthenticationWebFilter）之后，因此可以拿到已认证的身份信息（如果认证通过）<br>
 * 
 * @author Fangfang.Xu
 *
 */
@Component
public class OpenApiResponseSignGlobalFilter implements GlobalFilter, Ordered {

	public static final int ORDER = HIGHEST_PRECEDENCE;

	@Autowired
	private ServerCodecConfigurer codecConfigurer;
	@Autowired
	private Set<MessageBodyDecoder> bodyDecoders;
	@Autowired
	private Set<MessageBodyEncoder> bodyEncoders;
	@Autowired
	private NurseryGatewaySecurityProperties securityProperties;
	@Autowired
	private AppProvider appProvider;
	@Autowired
	private AuthMatcher authMatcher;

	private GatewayFilter delegatorFilter;

	@PostConstruct
	private void init() {
		NurseryGatewaySecurityProperties.Signature signature = securityProperties.getSignature();
		/**
		 * 只会对openapi signature模式起作用
		 */
		if (signature != null) {
			Config config = new ModifyResponseBodyGatewayFilterFactory.Config();
			/**
			 * 指定了String后，setRewriteFunction就只会进String类型<br>
			 * 下游服务的返回体是json String，ServerErrorGlobalFilter的返回体也是json String，因此这里String正合适
			 */
			config.setInClass(String.class);//进入json
			config.setOutClass(String.class);//response json

			config.setRewriteFunction((exc, obj) -> {
				ServerWebExchange exchange = (ServerWebExchange) exc;

				if (!authMatcher.isAuthPath(exchange)) {
					/**
					 * 不需要认证的请求只透传，不处理
					 */
					return Mono.just(obj);
				}

				OpenApiResponse openApiResponse;
				if (obj instanceof String) {
					/**
					 * 实际只会进这里
					 */
					openApiResponse = JsonUtils.deserialize(obj.toString(), OpenApiResponse.class);
				} else if (obj instanceof OpenApiResponse) {
					openApiResponse = (OpenApiResponse) obj;
				} else {
					/**
					 * 不可能到这里<br>
					 * 原样出去
					 */
					return Mono.just(obj);
				}

				OpenApiRequestBody requestBody = NurseryGatewayUtils.getOpenApiRequestBody(exchange);
				if (requestBody != null) {
					openApiResponse.setBiz_code(requestBody.getMethod());

					/**
					 * response签名
					 */
					App app = NurseryGatewayUtils.getApp(exchange);
					if (app != null) {
						String sign = NurseryGatewayUtils.responseSign(openApiResponse, requestBody.getSign_type(),
								app);
						openApiResponse.setSign(sign);
					}
				}

				return Mono.just(JsonUtils.serialize(openApiResponse));//要转成json string
			});

			/**
			 * copy from
			 * org.springframework.cloud.gateway.config.GatewayAutoConfiguration.modifyResponseBodyGatewayFilterFactory(ServerCodecConfigurer,
			 * Set<MessageBodyDecoder>, Set<MessageBodyEncoder>)
			 */
			ModifyResponseBodyGatewayFilterFactory factory = new ModifyResponseBodyGatewayFilterFactory(
					codecConfigurer.getReaders(), bodyDecoders, bodyEncoders);
			/**
			 * 这是 ModifyResponseGatewayFilter
			 */
			this.delegatorFilter = factory.apply(config);
		}
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		if (delegatorFilter == null) {
			return chain.filter(exchange);
		}

		return delegatorFilter.filter(exchange, chain);
	}

	@Override
	public int getOrder() {
		return ORDER;
	}
}