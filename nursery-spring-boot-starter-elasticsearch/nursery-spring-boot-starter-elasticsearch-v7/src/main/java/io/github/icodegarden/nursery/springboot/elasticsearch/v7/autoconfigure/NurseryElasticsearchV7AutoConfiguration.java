package io.github.icodegarden.nursery.springboot.elasticsearch.v7.autoconfigure;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springboot.elasticsearch.properties.NurseryElasticsearchProperties;
import io.github.icodegarden.nutrient.elasticsearch.ElasticsearchClientConfig;
import io.github.icodegarden.nutrient.elasticsearch.v7.ElasticsearchClientV7Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(ElasticsearchClientConfig.class)
@EnableConfigurationProperties({ NurseryElasticsearchProperties.class })
@Configuration
@Slf4j
public class NurseryElasticsearchV7AutoConfiguration {

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.elasticsearch.client.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public RestHighLevelClient restHighLevelClient(NurseryElasticsearchProperties elasticsearchProperties) {
		log.info("nursery init bean of RestHighLevelClient");

		elasticsearchProperties.validate();

		return ElasticsearchClientV7Builder.buildRestHighLevelClient(elasticsearchProperties);
	}
}
