package io.github.icodegarden.nursery.springboot.web.reactive.util.matcher;

import org.springframework.web.server.ServerWebExchange;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public interface ReactiveRequestMatcher {

	boolean matches(ServerWebExchange exchange);
}
