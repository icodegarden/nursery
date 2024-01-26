package io.github.icodegarden.nursery.springboot.web.reactive.factory;

import java.util.concurrent.ThreadFactory;

import org.springframework.util.ClassUtils;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class ServerInitiatedImmediateSchedulersFactory implements Schedulers.Factory {

	private static final boolean GATEWAY_PRESENT = ClassUtils
			.isPresent("org.springframework.cloud.gateway.filter.GlobalFilter", null);

	@Override
	public Scheduler newBoundedElastic(int threadCap, int queuedTaskCap, ThreadFactory threadFactory, int ttlSeconds) {
		if (shouldImmediate()) {
			return Schedulers.immediate();
		}
		return Schedulers.Factory.super.newBoundedElastic(threadCap, queuedTaskCap, threadFactory, ttlSeconds);
	}

	@Override
	public Scheduler newParallel(int parallelism, ThreadFactory threadFactory) {
		if (shouldImmediate()) {
			return Schedulers.immediate();
		}
		return Schedulers.Factory.super.newParallel(parallelism, threadFactory);
	}

	@Override
	public Scheduler newSingle(ThreadFactory threadFactory) {
		if (shouldImmediate()) {
			return Schedulers.immediate();
		}
		return Schedulers.Factory.super.newSingle(threadFactory);
	}

	/**
	 * 若是server代码发起的，则沿用原线程，避免进入Controller的线程不再是reactor-http-nio-*导致ReactorNetty.IO_WORKER_COUNT等参数失效<br>
	 */
	private boolean shouldImmediate() {
		String name = Thread.currentThread().getName();
		if (name.startsWith("reactor-http") && GATEWAY_PRESENT) {
			return false;
		}
		return "main".equals(name) || name.startsWith("reactor-http");// reactor-http-epoll or reactor-http-nio
	}
}