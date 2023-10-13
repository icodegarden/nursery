package io.github.icodegarden.nursery.springboot.elasticsearch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.icodegarden.nutrient.elasticsearch.ElasticsearchClientConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.elasticsearch")
@Getter
@Setter
@ToString(callSuper = true)
public class NurseryElasticsearchProperties extends ElasticsearchClientConfig {

}