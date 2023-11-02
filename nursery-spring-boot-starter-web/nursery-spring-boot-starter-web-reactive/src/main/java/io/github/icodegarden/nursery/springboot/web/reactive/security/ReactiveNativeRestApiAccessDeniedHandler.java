package io.github.icodegarden.nursery.springboot.web.reactive.security;

import java.nio.charset.Charset;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class ReactiveNativeRestApiAccessDeniedHandler implements ServerAccessDeniedHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
		if (exchange.getResponse().isCommitted()) {
			return Mono.empty();
		}
		
		return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
			response.setStatusCode(HttpStatus.FORBIDDEN);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

			String message = "Access Denied, Not Authorized.";

			if (log.isInfoEnabled()) {
				log.info("request {}", message);
			}

			DataBufferFactory dataBufferFactory = response.bufferFactory();
			DataBuffer buffer = dataBufferFactory.wrap(message.getBytes(Charset.forName("utf-8")));
			return response.writeWith(Mono.just(buffer)).doOnError((error) -> DataBufferUtils.release(buffer));
		});
	}

}
