package io.github.icodegarden.nursery.springboot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.endpoint")
@Getter
@Setter
@ToString
public class NurseryEndpointProperties {

	private Readiness readiness = new Readiness();

	@Getter
	@Setter
	@ToString
	public static class Readiness {
		private Long shutdownWaitMs = 30000L;
	}
}