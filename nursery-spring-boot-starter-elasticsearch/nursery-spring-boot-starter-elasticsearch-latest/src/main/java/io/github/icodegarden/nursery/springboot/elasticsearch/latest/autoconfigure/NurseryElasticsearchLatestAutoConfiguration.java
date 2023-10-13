package io.github.icodegarden.nursery.springboot.elasticsearch.latest.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import io.github.icodegarden.nursery.springboot.elasticsearch.properties.NurseryElasticsearchProperties;
import io.github.icodegarden.nutrient.elasticsearch.latest.ElasticsearchClientBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableConfigurationProperties({ NurseryElasticsearchProperties.class })
@Configuration
@Slf4j
public class NurseryElasticsearchLatestAutoConfiguration {

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.elasticsearch.client.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public ElasticsearchClient elasticsearchClient(NurseryElasticsearchProperties elasticsearchProperties) {
		log.info("nursery init bean of ElasticsearchClient");

		elasticsearchProperties.validate();

		return ElasticsearchClientBuilder.buildElasticsearchClient(elasticsearchProperties);
	}
}
