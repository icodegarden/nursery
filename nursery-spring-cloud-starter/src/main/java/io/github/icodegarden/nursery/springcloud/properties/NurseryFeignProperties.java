package io.github.icodegarden.nursery.springcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.feign")
@Getter
@Setter
@ToString
public class NurseryFeignProperties {

	private Header header = new Header();

	@Getter
	@Setter
	@ToString
	public static class Header {
		private boolean transferAll = false;
		private String userIdIfNotPresent = "sys";
		private String usernameIfNotPresent = "sys";
	}
}