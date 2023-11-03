package io.github.icodegarden.nursery.springboot.web.reactive.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.github.icodegarden.nursery.springboot.web.util.BaseWebUtils;
import io.github.icodegarden.nutrient.lang.annotation.Nullable;
import io.github.icodegarden.nutrient.lang.tuple.Tuple2;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.ReactorNetty;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class ReactiveWebUtils extends BaseWebUtils {
	private ReactiveWebUtils() {
	}

	private static final ThreadLocal<ServerWebExchange> SERVERWEBEXCHANGE_HOLDER = new ThreadLocal<>();

	@Getter
	private static enum ServerType {
		Gateway("500"/* 网关不是计算密集型，数量大比较好，配置成等于xxx.httpclient.pool.max-connections */, "500"), //
		General("200", "200"), //
		Compute("100", "100"), //
		IO("400", "400");

		private final String ioSelectCount = Runtime.getRuntime().availableProcessors() == 1 ? "1" : "2";
		private final String ioWorkerCount;
		private final String poolMaxConnections;

		private ServerType(String ioWorkerCount, String poolMaxConnections) {
			this.ioWorkerCount = ioWorkerCount;
			this.poolMaxConnections = poolMaxConnections;
		}
	}

	public static void initGatewayServerConfig() {
		initServerConfig(ServerType.Gateway);
	}

	public static void initGeneralServerConfig() {
		initServerConfig(ServerType.General);
	}

	public static void initComputeServerConfig() {
		initServerConfig(ServerType.Compute);
	}

	public static void initIoServerConfig() {
		initServerConfig(ServerType.IO);
	}

	private static void initServerConfig(ServerType type) {
		// --------------------------------------------------------------------
		String ioSelectCount = System.getProperty(ReactorNetty.IO_SELECT_COUNT);// 默认无
		log.info("found config IO_SELECT_COUNT is {}", ioSelectCount);
		if (!StringUtils.hasText(ioSelectCount)) {
			ioSelectCount = type.getIoSelectCount();
		}
		log.info("use IO_SELECT_COUNT:{}", ioSelectCount);
		System.setProperty(ReactorNetty.IO_SELECT_COUNT, ioSelectCount);

		// --------------------------------------------------------------------
		String ioWorkerCount = System.getProperty(ReactorNetty.IO_WORKER_COUNT);// 默认等于cpu线程数，但最少4
		log.info("found config IO_WORKER_COUNT is {}", ioWorkerCount);
		if (!StringUtils.hasText(ioWorkerCount)) {
			ioWorkerCount = type.getIoWorkerCount();
		}
		log.info("use IO_WORKER_COUNT:{}", ioWorkerCount);
		System.setProperty(ReactorNetty.IO_WORKER_COUNT, ioWorkerCount);

		// --------------------------------------------------------------------
		String poolMaxConnections = System.getProperty(ReactorNetty.POOL_MAX_CONNECTIONS);// 默认等于2*cpu线程数，但最少16
		log.info("found config POOL_MAX_CONNECTIONS is {}", poolMaxConnections);
		if (!StringUtils.hasText(poolMaxConnections)) {
			poolMaxConnections = type.getPoolMaxConnections();
		}
		log.info("use POOL_MAX_CONNECTIONS:{}", poolMaxConnections);
		System.setProperty(ReactorNetty.POOL_MAX_CONNECTIONS, poolMaxConnections);

		// --------------------------------------------------------------------
		/**
		 * 由server.netty.xxx配置
		 */
//		String poolMaxIdleTime = System.getProperty(ReactorNetty.POOL_MAX_IDLE_TIME);// 默认无配置
//		log.info("found config POOL_MAX_IDLE_TIME is {}", poolMaxIdleTime);
//		if (!StringUtils.hasText(poolMaxIdleTime)) {
//			poolMaxIdleTime = "";
//		}
//		log.info("use POOL_MAX_IDLE_TIME:{}", poolMaxIdleTime);
//		System.setProperty(ReactorNetty.POOL_MAX_IDLE_TIME, poolMaxIdleTime);

		// --------------------------------------------------------------------
		/**
		 * 由server.netty.xxx配置
		 */
//		String poolMaxLifeTime = System.getProperty(ReactorNetty.POOL_MAX_LIFE_TIME);// 默认无配置
//		log.info("found config POOL_MAX_LIFE_TIME is {}", poolMaxLifeTime);
//		if (!StringUtils.hasText(poolMaxLifeTime)) {
//			poolMaxLifeTime = "";
//		}
//		log.info("use POOL_MAX_LIFE_TIME:{}", poolMaxLifeTime);
//		System.setProperty(ReactorNetty.POOL_MAX_LIFE_TIME, poolMaxLifeTime);

//        String poolLeasingStrategy = System.getProperty(ReactorNetty.POOL_LEASING_STRATEGY);

	}

	public static ServerWebExchange getExchange() {
		return SERVERWEBEXCHANGE_HOLDER.get();
	}

	public static void setExchange(ServerWebExchange exchange) {
		SERVERWEBEXCHANGE_HOLDER.set(exchange);
	}

	public static List<String> getHeaderNames() {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return Collections.emptyList();
		}

		HttpHeaders headers = request.getRequest().getHeaders();
		return new ArrayList<String>(headers.keySet());
	}

	public static String getHeader(String name) {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return null;
		}

		return request.getRequest().getHeaders().getFirst(name);
	}

	public static String getJWT() {
		String bearerToken = getAuthorizationToken();
		if (bearerToken != null) {
			return resolveBearerToken(bearerToken, " ");
		}
		return null;
	}

	public static void setJWT(String jwt) {
		ServerWebExchange request = getExchange();
		if (request != null) {
			String bearerToken = createBearerToken(jwt, " ");
			request.getAttributes().put(HEADER_AUTHORIZATION, bearerToken);// use for rpc;
		}
	}

	public static String getBasicAuthorizationToken() {
		String basicToken = getAuthorizationToken();
		if (basicToken != null) {
			return resolveBasicToken(basicToken, " ");
		}
		return null;
	}

	public static String getAuthorizationToken() {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return null;
		}
		String authorizationToken = (String) request.getAttribute(HEADER_AUTHORIZATION);
		return authorizationToken != null ? authorizationToken
				: request.getRequest().getHeaders().getFirst(HEADER_AUTHORIZATION);
	}

	public static boolean isApiRpc() {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return false;
		}

		String header = request.getRequest().getHeaders().getFirst(HEADER_API_REQUEST);
		return header != null && Boolean.valueOf(header);
	}

	public static boolean isOpenapiRpc() {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return false;
		}

		String header = request.getRequest().getHeaders().getFirst(HEADER_OPENAPI_REQUEST);
		return header != null && Boolean.valueOf(header);
	}

	public static boolean isInternalRpc() {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return false;
		}

		String header = request.getRequest().getHeaders().getFirst(HEADER_INTERNAL_RPC);
		return header != null && Boolean.valueOf(header);
	}

	public static String getRequestId() {
		ServerWebExchange request = getExchange();
		if (request == null) {
			return null;
		}

		return request.getRequest().getHeaders().getFirst(HEADER_REQUEST_ID);
	}

	public static void responseJWT(String jwt, ServerWebExchange exchange) {
		String bearerToken = createBearerToken(jwt, " ");
		exchange.getResponse().getHeaders().set(HEADER_AUTHORIZATION, bearerToken);
	}

	public static <T> Mono<T> responseWrite(int status, String body, ServerWebExchange exchange) {
		Assert.hasText(body, "body must not empty");
		return responseWrite(status, null, body, exchange);
	}

	public static <T> Mono<T> responseWrite(int status, List<Tuple2<String, List<String>>> headers,
			ServerWebExchange exchange) {
		Assert.notEmpty(headers, "headers must not empty");
		return responseWrite(status, headers, null, exchange);
	}

	public static <T> Mono<T> responseWrite(int status, @Nullable List<Tuple2<String, List<String>>> headers,
			@Nullable String body, ServerWebExchange exchange) {
		exchange.getResponse().setRawStatusCode(status);
		if (headers != null && !headers.isEmpty()) {
			for (Tuple2<String, List<String>> header : headers) {
				for (String value : header.getT2()) {
					exchange.getResponse().getHeaders().add(header.getT1(), value);
				}
			}
		}

		if (body == null) {
			body = "";
		}

		exchange.getResponse().getHeaders().set("Content-Type", "application/json;charset=utf-8");
		DefaultDataBuffer dataBuffer = DefaultDataBufferFactory.sharedInstance
				.wrap(body.getBytes(StandardCharsets.UTF_8));
		return (Mono) exchange.getResponse().writeWith(Mono.just(dataBuffer));
	}
}
