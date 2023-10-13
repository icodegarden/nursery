package io.github.icodegarden.nursery.springboot.zookeeper.autoconfigure;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.client.ZKClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import io.github.icodegarden.nursery.springboot.zookeeper.properties.NurseryZookeeperProperties;
import io.github.icodegarden.nutrient.zookeeper.ACLsSupplier;
import io.github.icodegarden.nutrient.zookeeper.ZooKeeperHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@EnableConfigurationProperties({ NurseryZookeeperProperties.class })
@Configuration
@Slf4j
public class NurseryZookeeperAutoConfiguration {

	@ConditionalOnProperty(value = "icodegarden.nursery.zookeeper.client.enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public ZooKeeperHolder zooKeeperHolder(NurseryZookeeperProperties zookeeperProperties) {
		log.info("nursery init bean of ZooKeeperHolder");

		zookeeperProperties.validate();

		return new ZooKeeperHolder(zookeeperProperties);
	}

	@ConditionalOnProperty(value = "icodegarden.nursery.zookeeper.client.enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnClass(CuratorFramework.class)
	@Configuration
	public class CuratorFrameworkAutoConfiguration {

		@Bean
		public ACLsSupplier nullACLsSupplier() {
			return new ACLsSupplier.NullACLsSupplier();
		}

		@ConditionalOnMissingBean
		@Bean
		public CuratorFramework curatorFramework(NurseryZookeeperProperties zookeeperProperties,
				ACLsSupplier aclsSupplier) {
			log.info("nursery init bean of CuratorFramework");

			RetryPolicy retryPolicy = new RetryForever(3000);
			ZKClientConfig zkClientConfig = new ZKClientConfig();
			zkClientConfig.setProperty(ZKClientConfig.ZOOKEEPER_SERVER_PRINCIPAL,
					"zookeeper/" + zookeeperProperties.getConnectString());
			CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.getConnectString(),
					zookeeperProperties.getSessionTimeout(), zookeeperProperties.getConnectTimeout(),
					retryPolicy, zkClientConfig);

			if (!CollectionUtils.isEmpty(aclsSupplier.get())) {
				client.setACL().withACL(aclsSupplier.get());
			}

			client.start();
			return client;
		}
	}

}
