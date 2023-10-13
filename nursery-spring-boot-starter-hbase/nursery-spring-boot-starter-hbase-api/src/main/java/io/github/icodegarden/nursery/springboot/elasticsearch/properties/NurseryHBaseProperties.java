package io.github.icodegarden.nursery.springboot.elasticsearch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import io.github.icodegarden.nutrient.hbase.HBaseEnv;
import io.github.icodegarden.nutrient.lang.Validateable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.hbase")
@Getter
@Setter
@ToString(callSuper = true)
public class NurseryHBaseProperties implements Validateable {

	@NonNull
	private HBaseEnv.VersionFrom versionFrom;
	@NonNull
	private String hbaseZookeeperQuorum;

	private String hbaseClientUsername;//例如 root

	private String hbaseClientPassword;//例如 root
	/**
	 * 表名前缀
	 */
	private String namePrefix = "";
	
	@Override
	public void validate() throws IllegalArgumentException {
		Assert.notNull(versionFrom, "versionFrom must not null");
		Assert.hasText(hbaseZookeeperQuorum, "hbaseZookeeperQuorum must not empty");
	}

}