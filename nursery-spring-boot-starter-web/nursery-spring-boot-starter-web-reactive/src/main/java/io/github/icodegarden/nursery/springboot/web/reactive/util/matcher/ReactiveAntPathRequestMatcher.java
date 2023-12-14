package io.github.icodegarden.nursery.springboot.web.reactive.util.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;

import io.github.icodegarden.nursery.springboot.web.util.matcher.AbstractAntPathRequestMatcher;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public final class ReactiveAntPathRequestMatcher extends AbstractAntPathRequestMatcher<ServerWebExchange>
		implements ReactiveRequestMatcher {

	public ReactiveAntPathRequestMatcher(String pattern) {
		this(pattern, null);
	}

	public ReactiveAntPathRequestMatcher(String pattern, String httpMethod) {
		this(pattern, httpMethod, true);
	}

	public ReactiveAntPathRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
		super(pattern, httpMethod, caseSensitive);
	}

	@Override
	protected HttpMethod getHttpMethod(ServerWebExchange exchange) {
		return exchange.getRequest().getMethod();
	}

	@Override
	protected String getRequestPath(ServerWebExchange exchange) {
		return exchange.getRequest().getPath().value();
	}
}
