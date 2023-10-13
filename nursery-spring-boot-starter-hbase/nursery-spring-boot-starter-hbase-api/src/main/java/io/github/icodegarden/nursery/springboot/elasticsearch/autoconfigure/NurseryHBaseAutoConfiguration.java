package io.github.icodegarden.nursery.springboot.elasticsearch.autoconfigure;

import java.util.Properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import io.github.icodegarden.nursery.springboot.elasticsearch.properties.NurseryHBaseProperties;
import io.github.icodegarden.nutrient.hbase.HBaseEnv;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(HBaseEnv.class)
@EnableConfigurationProperties({ NurseryHBaseProperties.class })
@Configuration
@Slf4j
public class NurseryHBaseAutoConfiguration {

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.hbase.client.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public HBaseEnv hbaseEnv(NurseryHBaseProperties commonsHBaseProperties) {
		log.info("nursery init bean of HBaseEnv");
		
		commonsHBaseProperties.validate();

		Properties properties = new Properties();
		properties.setProperty("hbase.zookeeper.quorum", commonsHBaseProperties.getHbaseZookeeperQuorum());

		if (StringUtils.hasText(commonsHBaseProperties.getHbaseClientUsername())) {
			properties.setProperty("hbase.client.username", commonsHBaseProperties.getHbaseClientUsername());
		}
		if (StringUtils.hasText(commonsHBaseProperties.getHbaseClientPassword())) {
			properties.setProperty("hbase.client.password", commonsHBaseProperties.getHbaseClientPassword());
		}

		HBaseEnv hBaseEnv = new HBaseEnv(commonsHBaseProperties.getVersionFrom(), properties);
		hBaseEnv.setNamePrefix(commonsHBaseProperties.getNamePrefix());
		return hBaseEnv;
	}

}
