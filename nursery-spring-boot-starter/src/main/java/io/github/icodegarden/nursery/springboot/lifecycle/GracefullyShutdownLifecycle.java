package io.github.icodegarden.nursery.springboot.lifecycle;

import org.springframework.context.SmartLifecycle;

import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyShutdown;

/**
 * 通常你需要 @Bean
 * @author Fangfang.Xu
 *
 */
public class GracefullyShutdownLifecycle implements SmartLifecycle {

	private boolean running;

	@Override
	public void start() {
		running = true;
	}

	@Override
	public void stop() {
		running = false;

		GracefullyShutdown.Registry.singleton().shutdownRegistered();
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}