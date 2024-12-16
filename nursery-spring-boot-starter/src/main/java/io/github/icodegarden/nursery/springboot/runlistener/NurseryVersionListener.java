package io.github.icodegarden.nursery.springboot.runlistener;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import io.github.icodegarden.nursery.springboot.NurseryVersion;

/**
 * @author Fangfang.Xu
 */
public class NurseryVersionListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	private final NurseryVersion version;

	public NurseryVersionListener() {
		this(new NurseryVersion());
	}

	private NurseryVersionListener(NurseryVersion version) {
		this.version = version;
	}

	@Override
	public void onApplicationEvent(final ApplicationEnvironmentPreparedEvent event) {
		event.getEnvironment().getPropertySources().addFirst(version.getPropertiesPropertySource());
	}

}
