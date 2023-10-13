package io.github.icodegarden.nursery.springboot.web.servlet.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.DispatcherServlet;

import com.alibaba.csp.sentinel.SphU;

import io.github.icodegarden.nursery.springboot.web.properties.NurseryWebProperties;
import io.github.icodegarden.nursery.springboot.web.servlet.filter.ServletCacheRequestBodyFilter;
import io.github.icodegarden.nursery.springboot.web.servlet.filter.ServletGatewayPreAuthenticatedAuthenticationFilter;
import io.github.icodegarden.nursery.springboot.web.servlet.filter.ServletProcessingRequestCountFilter;
import io.github.icodegarden.nursery.springboot.web.servlet.handler.ServletApiResponseExceptionHandler;
import io.github.icodegarden.nursery.springboot.web.servlet.handler.ServletNativeRestApiExceptionHandler;
import io.github.icodegarden.nursery.springboot.web.servlet.handler.ServletSentinelAdaptiveApiResponseExceptionHandler;
import io.github.icodegarden.nursery.springboot.web.servlet.handler.ServletSentinelAdaptiveNativeRestApiExceptionHandler;
import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyShutdown;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableConfigurationProperties({ NurseryWebProperties.class })
@Configuration
@Slf4j
public class NurseryServletWebAutoConfiguration {

	public static final int FILTER_ORDER_PROCESSING_REQUEST_COUNT = Ordered.HIGHEST_PRECEDENCE;// 最高优先级
	public static final int FILTER_ORDER_GATEWAY_PRE_AUTHENTICATED_AUTHENTICATION = FILTER_ORDER_PROCESSING_REQUEST_COUNT
			+ 100;

	// ----------------------------------------------------------------------------------------

	/**
	 * 有webmvc，所以不会对gateway起作用 <br>
	 * spring对webmvc比webflux优先
	 * 
	 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
	 */
	@ConditionalOnClass({ DispatcherServlet.class })
	@Configuration
	protected static class WebMvcAutoConfiguration {

		@ConditionalOnProperty(value = "icodegarden.nursery.web.filter.cacheRequestBody.enabled", havingValue = "true", matchIfMissing = true)
		@Bean
		public FilterRegistrationBean<Filter> servletCacheRequestBodyFilter() {
			log.info("nursery init bean of CacheRequestBodyFilter");

			ServletCacheRequestBodyFilter filter = new ServletCacheRequestBodyFilter();

			FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<Filter>();
			bean.setFilter(filter);
			bean.setName("cacheRequestBodyFilter");
			bean.addUrlPatterns("/*");
			bean.setOrder(Ordered.LOWEST_PRECEDENCE);

			return bean;
		}

		@ConditionalOnProperty(value = "icodegarden.nursery.web.filter.processingRequestCount.enabled", havingValue = "true", matchIfMissing = true)
		@Bean
		public FilterRegistrationBean<Filter> servletProcessingRequestCountFilter() {
			log.info("nursery init bean of ProcessingRequestCountFilter");

			/**
			 * 下线优先级最低，30秒实例刷新间隔+10秒冗余
			 */
			ServletProcessingRequestCountFilter processingRequestCountFilter = new ServletProcessingRequestCountFilter(
					Integer.MAX_VALUE, 30 * 1000 + 10 * 1000/* 写死固定值，基本不需要配置化 */);

			FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<Filter>();
			bean.setFilter(processingRequestCountFilter);
			bean.setName("processingRequestCountFilter");
			bean.addUrlPatterns("/*");
			bean.setOrder(FILTER_ORDER_PROCESSING_REQUEST_COUNT);

			GracefullyShutdown.Registry.singleton().register(processingRequestCountFilter);

			return bean;
		}

		/**
		 * gateway是webflux因此不会有这个<br>
		 * 只对没有spring-security依赖的起作用，有依赖的认为自己认证
		 */
		@ConditionalOnMissingClass({ "org.springframework.security.core.Authentication" })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.filter.gatewayPreAuthenticatedAuthentication.enabled", havingValue = "true", matchIfMissing = true)
		@Bean
		public FilterRegistrationBean<Filter> servletGatewayPreAuthenticatedAuthenticationFilter() {
			log.info("nursery init bean of GatewayPreAuthenticatedAuthenticationFilter");

			ServletGatewayPreAuthenticatedAuthenticationFilter filter = new ServletGatewayPreAuthenticatedAuthenticationFilter();

			FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<Filter>();
			bean.setFilter(filter);
			bean.setName("gatewayPreAuthenticatedAuthenticationFilter");
			bean.addUrlPatterns("/*");
			bean.setOrder(FILTER_ORDER_GATEWAY_PRE_AUTHENTICATED_AUTHENTICATION);

			return bean;
		}

		/**
		 * 内部依赖javax.servlet.<br>
		 * 有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherServlet.class, SphU.class })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.apiResponse.enabled"/* mvc和flux使用相同的配置名 */, havingValue = "true", matchIfMissing = true)
		@Configuration
		protected static class SentinelAdaptiveApiResponseExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ServletSentinelAdaptiveApiResponseExceptionHandler servletSentinelAdaptiveApiResponseExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of SentinelAdaptiveApiResponseExceptionHandler");
				ServletSentinelAdaptiveApiResponseExceptionHandler exceptionHandler = new ServletSentinelAdaptiveApiResponseExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 内部依赖javax.servlet.<br>
		 * 有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherServlet.class, SphU.class })
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.nativeRestApi.enabled"/* mvc和flux使用相同的配置名 */, havingValue = "true", matchIfMissing = false)
		@Configuration
		protected static class SentinelAdaptiveNativeRestApiExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ServletSentinelAdaptiveNativeRestApiExceptionHandler servletSentinelAdaptiveNativeRestApiExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of SentinelAdaptiveNativeRestApiExceptionHandler");
				ServletSentinelAdaptiveNativeRestApiExceptionHandler exceptionHandler = new ServletSentinelAdaptiveNativeRestApiExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 内部依赖javax.servlet.<br>
		 * 有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherServlet.class })
		@ConditionalOnMissingClass("com.alibaba.csp.sentinel.SphU")
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.apiResponse.enabled"/* mvc和flux使用相同的配置名 */, havingValue = "true", matchIfMissing = true)
		@Configuration
		protected static class ApiResponseExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ServletApiResponseExceptionHandler servletApiResponseExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of ApiResponseExceptionHandler");
				ServletApiResponseExceptionHandler exceptionHandler = new ServletApiResponseExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}

		/**
		 * 内部依赖javax.servlet.<br>
		 * 有webmvc <br>
		 * 
		 * @see org.springframework.boot.WebApplicationType.deduceFromClasspath()
		 */
		@ConditionalOnClass({ DispatcherServlet.class })
		@ConditionalOnMissingClass("com.alibaba.csp.sentinel.SphU")
		@ConditionalOnProperty(value = "icodegarden.nursery.web.exceptionHandler.nativeRestApi.enabled"/* mvc和flux使用相同的配置名 */, havingValue = "true", matchIfMissing = false)
		@Configuration
		protected static class NativeRestApiExceptionHandlerAutoConfiguration {
			@ConditionalOnMissingBean
			@Bean
			public ServletNativeRestApiExceptionHandler servletNativeRestApiExceptionHandler(
					NurseryWebProperties commonsWebProperties) {
				log.info("nursery init bean of NativeRestApiExceptionHandler");
				ServletNativeRestApiExceptionHandler exceptionHandler = new ServletNativeRestApiExceptionHandler();
				exceptionHandler.setPrintErrorStackOnWarn(
						commonsWebProperties.getExceptionHandler().getPrintErrorStackOnWarn());
				return exceptionHandler;
			}
		}
	}

}
