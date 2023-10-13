package io.github.icodegarden.nursery.springboot.zookeeper.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.icodegarden.nutrient.zookeeper.ZooKeeperHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.zookeeper")
@Getter
@Setter
@ToString(callSuper = true)
public class NurseryZookeeperProperties extends ZooKeeperHolder.Config {

}