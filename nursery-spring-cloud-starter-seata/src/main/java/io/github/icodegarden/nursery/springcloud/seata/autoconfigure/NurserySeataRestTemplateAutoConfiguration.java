package io.github.icodegarden.nursery.springcloud.seata.autoconfigure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import io.github.icodegarden.nursery.springcloud.seata.SeataRestTemplateInterceptor;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.PostConstruct;
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
public class NurserySeataRestTemplateAutoConfiguration {

	{
		log.info("nursery init bean of NurserySeataRestTemplateAutoConfiguration");
	}

	@ConditionalOnClass({ RestTemplate.class })
	@Bean
	public SeataRestTemplateConfigBean seataRestTemplateConfigBean() {
		return new SeataRestTemplateConfigBean();
	}

	private class SeataRestTemplateConfigBean {

		@Autowired(required = false)
		private Collection<RestTemplate> restTemplates;

		@PostConstruct
		public void init() {
			if (this.restTemplates != null) {
				SeataRestTemplateInterceptor seataRestTemplateInterceptor = new SeataRestTemplateInterceptor();

				for (RestTemplate restTemplate : restTemplates) {
					List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>(
							restTemplate.getInterceptors());
					interceptors.add(seataRestTemplateInterceptor);
					restTemplate.setInterceptors(interceptors);
				}
			}
		}

	}

}