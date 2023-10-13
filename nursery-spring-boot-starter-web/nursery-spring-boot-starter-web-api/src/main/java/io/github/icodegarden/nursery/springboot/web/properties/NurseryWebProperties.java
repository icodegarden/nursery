package io.github.icodegarden.nursery.springboot.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.web")
@Getter
@Setter
@ToString
public class NurseryWebProperties {

	private ExceptionHandler exceptionHandler = new ExceptionHandler();

	@Getter
	@Setter
	@ToString
	public static class ExceptionHandler {
		private Boolean printErrorStackOnWarn = true;
	}
}