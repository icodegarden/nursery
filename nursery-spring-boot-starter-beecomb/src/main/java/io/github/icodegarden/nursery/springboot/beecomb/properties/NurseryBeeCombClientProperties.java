package io.github.icodegarden.nursery.springboot.beecomb.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.icodegarden.beecomb.client.ClientProperties;
import io.github.icodegarden.beecomb.client.ZooKeeperClientProperties;
import io.github.icodegarden.beecomb.common.properties.ZooKeeper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.beecomb.client")
@Getter
@Setter
@ToString
public class NurseryBeeCombClientProperties {

	private BasicAuthentication basicAuth = new BasicAuthentication();

	private ZooKeeper zookeeper = new ZooKeeper();

	private Master master = new Master();

	private ClientProperties.Exchange exchange = new ClientProperties.Exchange();

	private ZooKeeperClientProperties.LoadBalance loadBalance = new ZooKeeperClientProperties.LoadBalance();

	@Getter
	@Setter
	@ToString
	public class BasicAuthentication {
		private String username;
		private String password;
	}

	@Getter
	@Setter
	@ToString
	public class Master {
		/**
		 * http://1.1.1.1:9898,http://1.1.1.2:9898 ...
		 */
		private String httpHosts;
	}
}