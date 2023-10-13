package io.github.icodegarden.nursery.springboot.shardingsphere.autoconfigure;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springboot.shardingsphere.properties.NurseryShardingSphereProperties;
import io.github.icodegarden.nutrient.shardingsphere.builder.DataSourceOnlyApiShardingSphereBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@ConditionalOnClass({ ShardingSphereDataSource.class })
@EnableConfigurationProperties({ NurseryShardingSphereProperties.class })
@Configuration
@Slf4j
public class NurseryShardingSphereAutoConfiguration {

	{
		log.info("nursery init bean of NurseryShardingSphereAutoConfiguration");
	}

	/**
	 * sharding DataSource
	 */
	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.shardingsphere.datasource.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public DataSource dataSource(NurseryShardingSphereProperties properties) throws SQLException {
		DataSource dataSource = DataSourceOnlyApiShardingSphereBuilder.getDataSource(properties);
		return dataSource;
	}
}