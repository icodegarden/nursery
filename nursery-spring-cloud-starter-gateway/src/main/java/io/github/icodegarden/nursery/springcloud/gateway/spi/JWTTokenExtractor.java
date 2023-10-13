package io.github.icodegarden.nursery.springcloud.gateway.spi;

import org.springframework.web.server.ServerWebExchange;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public interface JWTTokenExtractor {

	/**
	 * @return jwt token
	 */
	String extract(ServerWebExchange exchange);
}
