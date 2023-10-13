package io.github.icodegarden.nursery.springcloud.sentinel.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.csp.sentinel.SphU;

import io.github.icodegarden.nursery.springcloud.sentinel.SentinelClusterClientStarter;
import io.github.icodegarden.nursery.springcloud.sentinel.SentinelEventObserverStarter;
import io.github.icodegarden.nursery.springcloud.sentinel.SentinelNacosDynamicRuleStarter;
import io.github.icodegarden.nursery.springcloud.sentinel.properties.NurserySentinelProperties;
import io.github.icodegarden.nursery.springcloud.sentinel.properties.NurserySentinelProperties.Cluster;
import io.github.icodegarden.nursery.springcloud.sentinel.properties.NurserySentinelProperties.Nacos;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass({ SphU.class })
@EnableConfigurationProperties({ NurserySentinelProperties.class })
@Configuration
@Slf4j
public class NurserySentinelAutoConfiguration {

	{
		log.info("nursery init bean of NurserySentinelAutoConfiguration");
	}

	/**
	 * @Order 作用基础的要在SentinelNacosDynamicRuleAutoConfiguration前执行，否则当没有引入spring-cloud-starter-alibaba-sentinel时会导致不去连接dashboard
	 */
	@Order(-1)
	@Configuration
	public static class SentinelEnvAutoConfiguration {
		@Autowired
		private Environment env;
		@Autowired
		private NurserySentinelProperties sentinelProperties;

		@PostConstruct
		private void init() {
			log.info("nursery init bean of SentinelEnvAutoConfiguration");

			/**
			 * 基础
			 */
			if (!ClassUtils.isPresent("com.alibaba.cloud.sentinel.SentinelProperties", null)) {
				/**
				 * 当没有依赖spring-cloud-starter-alibaba-sentinel，要在sentinel初始化前设置-D参数地址，而不能通过application.yml自动生效
				 */
				String dashboardAddr = env.getProperty("spring.cloud.sentinel.transport.dashboard");
				log.info(
						"this project maven dependency not contains spring-cloud-starter-alibaba-sentinel, will set sentinel dashboard:{}",
						dashboardAddr);
				Assert.hasText(dashboardAddr,
						"dashboardAddr must not empty when dependency not contains spring-cloud-starter-alibaba-sentinel");
				System.setProperty("csp.sentinel.dashboard.server", dashboardAddr);// IMPT

				String projectName = env.getRequiredProperty("spring.application.name");
				System.setProperty("project.name", projectName);// IMPT
			}
			SentinelEventObserverStarter.addDefaultLoggingObserver();

			/**
			 * 集群
			 */
			Cluster cluster = sentinelProperties.getCluster();
			log.info("sentinel cluster is enbaled:{}", cluster.getEnabled());
			if (cluster.getEnabled()) {
				String projectName = env.getRequiredProperty("spring.application.name");
				SentinelClusterClientStarter.start(cluster.getServerAddr(), cluster.getServerPort(), projectName);
			}
		}
	}

	@ConditionalOnBean(NacosConfigProperties.class)
	@ConditionalOnClass({ SphU.class })
	@ConditionalOnProperty(value = "icodegarden.nursery.sentinel.nacos.rule.enabled", havingValue = "true", matchIfMissing = true)
	@Configuration
	public static class SentinelNacosDynamicRuleAutoConfiguration {
		@Autowired
		private Environment env;
		@Autowired
		private NacosConfigProperties nacosConfigProperties;
		@Autowired
		private NurserySentinelProperties sentinelProperties;

		@PostConstruct
		private void init() {
			log.info("nursery init bean of DynamicRuleAutoConfiguration");

			String dataId = env.getRequiredProperty("spring.application.name");
			Nacos nacos = sentinelProperties.getNacos();

			SentinelNacosDynamicRuleStarter.start(nacosConfigProperties, dataId, nacos.getDynamicRuleGroupId());
		}
	}
}