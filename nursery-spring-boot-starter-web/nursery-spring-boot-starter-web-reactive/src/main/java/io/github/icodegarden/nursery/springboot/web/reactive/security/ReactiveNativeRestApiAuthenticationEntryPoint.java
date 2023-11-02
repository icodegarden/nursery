package io.github.icodegarden.nursery.springboot.web.reactive.security;

import java.nio.charset.Charset;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * InternalApi/OpenApi认证失败时的统一响应
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class ReactiveNativeRestApiAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private static final Charset CHARSET = Charset.forName("utf-8");

	/**
	 * 认证失败时
	 */
	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
		if (exchange.getResponse().isCommitted()) {
			return Mono.empty();
		}

		return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			DataBufferFactory dataBufferFactory = response.bufferFactory();

			String message = (e.getMessage() != null ? e.getMessage() : "Not Authenticated.");

			if (log.isInfoEnabled()) {
				String path = null;
				try {
					path = exchange.getRequest().getPath().toString();
				} catch (Exception ex) {
					log.error("ex on get request path", ex);
				}

				log.info("path of {} request Authentication failed:{}", path, message);
			}

			DataBuffer buffer = dataBufferFactory.wrap(message.getBytes(CHARSET));
			return response.writeWith(Mono.just(buffer)).doOnError((error) -> DataBufferUtils.release(buffer));
		});
	}
}
