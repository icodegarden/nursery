package io.github.icodegarden.nursery.springcloud.seata.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import feign.RequestInterceptor;
import io.seata.core.context.RootContext;
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
public class NurserySeataFeignAutoConfiguration {

	{
		log.info("nursery init bean of NurserySeataFeignAutoConfiguration");
	}

	@ConditionalOnClass({ RequestInterceptor.class })
	@Bean
	public RequestInterceptor seataRequestInterceptor() {
		return new SeataRequestInterceptor();
	}

	private class SeataRequestInterceptor implements RequestInterceptor {
		@Override
		public void apply(feign.RequestTemplate template) {
			String xid = RootContext.getXID();
			if (!StringUtils.isEmpty(xid)) {
				template.header(RootContext.KEY_XID, xid);
			}
		}
	}

}