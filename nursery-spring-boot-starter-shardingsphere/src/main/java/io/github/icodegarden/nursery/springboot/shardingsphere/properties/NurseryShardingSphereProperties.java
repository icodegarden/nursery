package io.github.icodegarden.nursery.springboot.shardingsphere.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.icodegarden.nutrient.shardingsphere.builder.DataSourceOnlyConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.shardingsphere")
@Getter
@Setter
@ToString
public class NurseryShardingSphereProperties extends DataSourceOnlyConfig {

}