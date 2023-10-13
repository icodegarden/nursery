package io.github.icodegarden.nursery.springcloud.gateway.core.security;

import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import io.github.icodegarden.nursery.springboot.security.User;
import io.github.icodegarden.nursery.springboot.web.util.WebUtils;
import io.github.icodegarden.nursery.springcloud.loadbalancer.FlowTagLoadBalancer;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class UserServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		return Mono.defer(() -> {
			WebFilterChain chain = webFilterExchange.getChain();
			ServerWebExchange exchange = webFilterExchange.getExchange();

			User principal = (User) authentication.getPrincipal();
			Map<String, Object> details = (Map) authentication.getDetails();

			ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
				httpHeaders.add(WebUtils.HEADER_API_REQUEST, "true");
				
				httpHeaders.add(WebUtils.HEADER_USERID, principal.getUserId());
				httpHeaders.add(WebUtils.HEADER_USERNAME, principal.getUsername());
				if (details != null) {
					String flowTagRequired = (String) details.get(FlowTagLoadBalancer.HTTPHEADER_FLOWTAG_REQUIRED);
					String flowTagFirst = (String) details.get(FlowTagLoadBalancer.HTTPHEADER_FLOWTAG_FIRST);
					httpHeaders.add(FlowTagLoadBalancer.HTTPHEADER_FLOWTAG_REQUIRED, flowTagRequired);
					httpHeaders.add(FlowTagLoadBalancer.HTTPHEADER_FLOWTAG_FIRST, flowTagFirst);
				}
			}).build();

			return chain.filter(exchange.mutate().request(request).build());
		});
	}
}