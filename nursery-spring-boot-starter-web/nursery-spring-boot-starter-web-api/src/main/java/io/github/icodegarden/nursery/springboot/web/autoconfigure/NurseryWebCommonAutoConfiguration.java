package io.github.icodegarden.nursery.springboot.web.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import io.github.icodegarden.nursery.springboot.web.util.MappingJackson2HttpMessageConverters;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
@Slf4j
public class NurseryWebCommonAutoConfiguration {

	/**
	 * 公共的
	 */
	@ConditionalOnClass({ MappingJackson2HttpMessageConverter.class })
	@ConditionalOnProperty(value = "icodegarden.nursery.web.converter.mappingJackson.enabled", havingValue = "true", matchIfMissing = true)
	@Configuration
	protected static class MappingJackson2HttpMessageConverterAutoConfiguration {

		@Bean
		public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
			log.info("nursery init bean of MappingJackson2HttpMessageConverter");
			return MappingJackson2HttpMessageConverters.simple();
		}
	}

}
