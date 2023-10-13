package io.github.icodegarden.nursery.springcloud.gateway.predicate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import io.github.icodegarden.nursery.springcloud.gateway.util.NurseryGatewayUtils;
import io.github.icodegarden.nutrient.lang.annotation.NotEmpty;
import io.github.icodegarden.nutrient.lang.spec.sign.OpenApiRequestBody;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class BodyMethodRoutePredicateFactory
		extends AbstractRoutePredicateFactory<BodyMethodRoutePredicateFactory.Config> {

	/**
	 * Methods key.
	 */
	public static final String BODYMETHOD_KEY = "bodyMethod";

	public BodyMethodRoutePredicateFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(BODYMETHOD_KEY);
	}

//	@Override
//	public ShortcutType shortcutType() {
//		return ShortcutType.GATHER_LIST_TAIL_FLAG;
//	}

	@Override
	public Predicate<ServerWebExchange> apply(Config config) {
		return new GatewayPredicate() {
			@Override
			public boolean test(ServerWebExchange exchange) {
				OpenApiRequestBody requestBody = NurseryGatewayUtils.getOpenApiRequestBody(exchange);
				if(requestBody == null) {
					return false;
				}
				String method = requestBody.getMethod();
				return config.getBodyMethod().equals(method);
			}

			@Override
			public Object getConfig() {
				return config;
			}

			@Override
			public String toString() {
				return String.format("BodyMethod: %s", config.getBodyMethod());
			}
		};
	}

	@Validated
	public static class Config {

		@NotEmpty
		private String bodyMethod;

		public String getBodyMethod() {
			return bodyMethod;
		}

		public void setBodyMethod(String bodyMethod) {
			this.bodyMethod = bodyMethod;
		}
	}

}