package io.github.icodegarden.nursery.springboot.beecomb.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.icodegarden.beecomb.common.properties.ZooKeeper;
import io.github.icodegarden.beecomb.executor.ZooKeeperSupportInstanceProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.beecomb.executor")
@Getter
@Setter
@ToString(callSuper = true)
public class NurseryBeeCombExecutorProperties extends ZooKeeperSupportInstanceProperties {

	public NurseryBeeCombExecutorProperties() {
		super(new ZooKeeper());
	}

	public NurseryBeeCombExecutorProperties(ZooKeeper zookeeper) {
		super(zookeeper);
	}

}