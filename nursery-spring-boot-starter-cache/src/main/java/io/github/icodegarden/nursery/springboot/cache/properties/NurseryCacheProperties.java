package io.github.icodegarden.nursery.springboot.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.cache")
@Getter
@Setter
@ToString
public class NurseryCacheProperties {

	public static NurseryCacheProperties INSTANCE;

	private int expireSeconds = 1800;

	public NurseryCacheProperties() {
		INSTANCE = this;
	}
}