package io.github.icodegarden.nursery.springboot.beecomb.autoconfigure;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import io.github.icodegarden.beecomb.client.BeeCombClient;
import io.github.icodegarden.beecomb.client.ClientProperties;
import io.github.icodegarden.beecomb.client.ClientProperties.Exchange;
import io.github.icodegarden.beecomb.client.UrlsBeeCombClient;
import io.github.icodegarden.beecomb.client.UrlsClientProperties;
import io.github.icodegarden.beecomb.client.ZooKeeperBeeCombClient;
import io.github.icodegarden.beecomb.client.ZooKeeperClientProperties;
import io.github.icodegarden.beecomb.client.ZooKeeperClientProperties.LoadBalance;
import io.github.icodegarden.beecomb.client.security.Authentication;
import io.github.icodegarden.beecomb.client.security.BasicAuthentication;
import io.github.icodegarden.beecomb.common.properties.ZooKeeper;
import io.github.icodegarden.nursery.springboot.beecomb.properties.NurseryBeeCombClientProperties;
import io.github.icodegarden.nursery.springboot.beecomb.properties.NurseryBeeCombClientProperties.Master;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(BeeCombClient.class)
@EnableConfigurationProperties({ NurseryBeeCombClientProperties.class })
@Configuration
@Slf4j
public class NurseryBeeCombClientAutoConfiguration {

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.beecomb.client.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public BeeCombClient beeCombClient(NurseryBeeCombClientProperties beeCombClientProperties) {
		log.info("nursery init bean of BeeCombClient");

		NurseryBeeCombClientProperties.BasicAuthentication basicAuth = beeCombClientProperties.getBasicAuth();

		Authentication authentication = new BasicAuthentication(basicAuth.getUsername(), basicAuth.getPassword());

		ZooKeeper zkProps = beeCombClientProperties.getZookeeper();
		if (StringUtils.hasText(zkProps.getConnectString())) {
			ZooKeeper zooKeeper = new ZooKeeper();
			BeanUtils.copyProperties(zkProps, zooKeeper);

			ZooKeeperClientProperties clientProperties = new ZooKeeperClientProperties(authentication, zooKeeper);

			Exchange exchange = new ClientProperties.Exchange();
			BeanUtils.copyProperties(beeCombClientProperties.getExchange(), exchange);
			clientProperties.setExchange(exchange);

			LoadBalance loadBalance = new ZooKeeperClientProperties.LoadBalance();
			BeanUtils.copyProperties(beeCombClientProperties.getLoadBalance(), loadBalance);
			clientProperties.setLoadBalance(loadBalance);

			return new ZooKeeperBeeCombClient(clientProperties);
		}

		Master master = beeCombClientProperties.getMaster();
		if (StringUtils.hasText(master.getHttpHosts())) {
			List<String> httpHosts = Arrays.asList(master.getHttpHosts().split(","));
			UrlsClientProperties clientProperties = new UrlsClientProperties(authentication, httpHosts);
			
			Exchange exchange = new ClientProperties.Exchange();
			BeanUtils.copyProperties(beeCombClientProperties.getExchange(), exchange);
			clientProperties.setExchange(exchange);
			
			return new UrlsBeeCombClient(clientProperties);
		}

		throw new IllegalArgumentException(
				"zooKeeper or master must config on init BeeCombClient, Properties:" + beeCombClientProperties);
	}
}
