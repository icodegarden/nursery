package io.github.icodegarden.nursery.springcloud.seata.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.icodegarden.nursery.springcloud.seata.SeataHandlerInterceptor;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnProperty(value = "icodegarden.nursery.seata.springcloud.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({ GlobalTransactional.class })
/**
 * 有这个说明引了spring-cloud-alibaba-seata，则由spring-cloud-alibaba-seata起作用
 */
@ConditionalOnMissingClass("com.alibaba.cloud.seata.web.SeataHandlerInterceptor")
@Configuration
@Slf4j
public class NurserySeataServletWebAutoConfiguration {

	{
		log.info("nursery init bean of NurserySeataServletWebAutoConfiguration");
	}

	@ConditionalOnWebApplication(type = Type.SERVLET)
	@Bean
	public SeataHandlerInterceptorConfigBean seataHandlerInterceptorConfigBean() {
		return new SeataHandlerInterceptorConfigBean();
	}

	private class SeataHandlerInterceptorConfigBean implements WebMvcConfigurer {

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new SeataHandlerInterceptor()).addPathPatterns("/**");
		}
	}

}