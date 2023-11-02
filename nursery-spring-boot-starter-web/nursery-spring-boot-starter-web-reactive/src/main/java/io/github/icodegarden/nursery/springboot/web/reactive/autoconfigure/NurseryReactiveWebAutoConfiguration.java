package io.github.icodegarden.nursery.springboot.web.reactive.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.WebFilter;

import com.alibaba.csp.sentinel.SphU;

import io.github.icodegarden.nursery.springboot.web.properties.NurseryWebProperties;
import io.github.icodegarden.nursery.springboot.web.reactive.filter.ReactiveGatewayPreAuthenticatedAuthenticationFilter;
import io.github.icodegarden.nursery.springboot.web.reactive.filter.ReactiveProcessingRequestCountFilter;
import io.github.icodegarden.nursery.springboot.web.reactive.handler.ReactiveApiResponseExceptionHandler;
import io.github.icodegarden.nursery.springboot.web.reactive.handler.ReactiveControllerAspect;
import io.github.icodegarden.nursery.springboot.web.reactive.handler.ReactiveNativeRestApiExceptionHandler;
import io.github.icodegarden.nursery.springboot.web.reactive.handler.ReactiveSentinelAdaptiveApiResponseExceptionHandler;
import io.github.icodegarden.nursery.springboot.web.reactive.handler.ReactiveSentinelAdaptiveNativeRestApiExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableConfigurationProperties({ NurseryWebProperties.class })
@Configuration
@Slf4j
public class NurseryReactiveWebAutoConfiguration {

	public static final int FILTER_ORDER_PROCESSING_REQUEST_COUNT = Ordered.HIGHEST_PRECEDENCE;// 最高优先级
	public static final int FILTER_ORDER_GATEWAY_PRE_AUTHENTICATED_AUTHENTICATION = FILTER_ORDER_PROCESSING_REQUEST_COUNT
			+ 100;

	// ----------------------------------------------------------------------------------------

	/**
	 * 有webflux，且没有webmvc <br>
	 * 
	 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
	 */
	@ConditionalOnClass({ DispatcherHandler.class })
	@ConditionalOnMissingClass({ "org.springframework.web.servlet.DispatcherServlet",
			"org.glassfish.jersey.servlet.ServletContainer" })
	@Configuration
	protected static class WebFluxAutoConfiguration {

		/**
		 * 暂无Flux的CacheRequestBody
		 */

		/**
		 * gateway不需要这个<br>
		 */
		@ConditionalOnMissingClass({ "org.springframework.cloud.gateway.filter.GatewayFilter"/* gateway不需要这个 */ })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.filter.processingRequestCount.enabled", havingValue = "true", matchIfMissing = true)
		@Bean
		public WebFilter reactiveProcessingRequestCountFilter() {
			log.info("nursery init bean of ReactiveProcessingRequestCountFilter");

			/**
			 * 下线优先级最低，30秒实例刷新间隔+10秒冗余
			 */
			ReactiveProcessingRequestCountFilter filter = new ReactiveProcessingRequestCountFilter(Integer.MAX_VALUE,
					30 * 1000 + 10 * 1000/* 写死固定值，基本不需要配置化 */);
			filter.setOrder(FILTER_ORDER_PROCESSING_REQUEST_COUNT);

			/*
			 * 由NurseryBeanAutoConfiguration无损下线注册
			 */
//			GracefullyShutdown.Registry.singleton().register(filter);

			return filter;
		}

		/**
		 * gateway不需要这个<br>
		 * 只对没有spring-security依赖的起作用，有依赖的认为自己认证
		 */
		@ConditionalOnMissingClass({ "org.springframework.security.core.Authentication",
				"org.springframework.cloud.gateway.filter.GatewayFilter"/* gateway不需要这个 */ })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.filter.gatewayPreAuthenticatedAuthentication.enabled", havingValue = "true", matchIfMissing = true)
		@Bean
		public WebFilter reactiveGatewayPreAuthenticatedAuthenticationFilter() {
			log.info("nursery init bean of ReactiveGatewayPreAuthenticatedAuthenticationFilter");

			ReactiveGatewayPreAuthenticatedAuthenticationFilter filter = new ReactiveGatewayPreAuthenticatedAuthenticationFilter();
			filter.setOrder(FILTER_ORDER_GATEWAY_PRE_AUTHENTICATED_AUTHENTICATION);

			return filter;
		}

		/**
		 * 
		 * 有webflux且没有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherHandler.class, SphU.class })
		@ConditionalOnMissingClass({ "org.springframework.web.servlet.DispatcherServlet",
				"org.glassfish.jersey.servlet.ServletContainer" })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.apiResponse.enabled"/*
																										 * mvc和flux使用相同的配置名
																										 */, havingValue = "true", matchIfMissing = true)
		@Configuration
		protected static class SentinelAdaptiveApiResponseReactiveExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ReactiveSentinelAdaptiveApiResponseExceptionHandler reactiveSentinelAdaptiveApiResponseExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of SentinelAdaptiveApiResponseReactiveExceptionHandler");
				ReactiveSentinelAdaptiveApiResponseExceptionHandler exceptionHandler = new ReactiveSentinelAdaptiveApiResponseExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 
		 * 有webflux且没有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherHandler.class, SphU.class })
		@ConditionalOnMissingClass({ "org.springframework.web.servlet.DispatcherServlet",
				"org.glassfish.jersey.servlet.ServletContainer" })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.nativeRestApi.enabled"/*
																										 * mvc和flux使用相同的配置名
																										 */, havingValue = "true", matchIfMissing = false)
		@Configuration
		protected static class SentinelAdaptiveNativeRestApiReactiveExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ReactiveSentinelAdaptiveNativeRestApiExceptionHandler reactiveSentinelAdaptiveNativeRestApiExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of SentinelAdaptiveNativeRestReactiveApiExceptionHandler");
				ReactiveSentinelAdaptiveNativeRestApiExceptionHandler exceptionHandler = new ReactiveSentinelAdaptiveNativeRestApiExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 
		 * 有webflux且没有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherHandler.class })
		@ConditionalOnMissingClass({ "org.springframework.web.servlet.DispatcherServlet",
				"org.glassfish.jersey.servlet.ServletContainer", "com.alibaba.csp.sentinel.SphU" })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.apiResponse.enabled"/*
																										 * mvc和flux使用相同的配置名
																										 */, havingValue = "true", matchIfMissing = true)
		@Configuration
		protected static class ApiResponseReactiveExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ReactiveApiResponseExceptionHandler reactiveApiResponseExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of ApiResponseReactiveExceptionHandler");
				ReactiveApiResponseExceptionHandler exceptionHandler = new ReactiveApiResponseExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 
		 * 有webflux且没有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherHandler.class })
		@ConditionalOnMissingClass({ "org.springframework.web.servlet.DispatcherServlet",
				"org.glassfish.jersey.servlet.ServletContainer", "com.alibaba.csp.sentinel.SphU" })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.nativeRestApi.enabled"/*
																										 * mvc和flux使用相同的配置名
																										 */, havingValue = "true", matchIfMissing = false)
		@Configuration
		protected static class NativeRestApiReactiveExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ReactiveNativeRestApiExceptionHandler reactiveNativeRestApiExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of NativeRestApiReactiveExceptionHandler");
				ReactiveNativeRestApiExceptionHandler exceptionHandler = new ReactiveNativeRestApiExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 
		 * 有webflux且没有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherHandler.class })
		@ConditionalOnMissingClass({ "org.springframework.web.servlet.DispatcherServlet",
				"org.glassfish.jersey.servlet.ServletContainer" })
		@Configuration
		protected static class ReactiveControllerAspectAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ReactiveControllerAspect reactiveControllerAspect() {
				log.info("nursery init bean of ReactiveControllerAspect");
				return new ReactiveControllerAspect();
			}
		}
	}
}
