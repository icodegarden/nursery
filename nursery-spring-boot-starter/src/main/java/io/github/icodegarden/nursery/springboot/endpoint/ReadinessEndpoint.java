package io.github.icodegarden.nursery.springboot.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import io.github.icodegarden.nursery.springboot.properties.NurseryEndpointProperties;
import io.github.icodegarden.nutrient.lang.lifecycle.GracefullyShutdown;
import lombok.extern.slf4j.Slf4j;

/**
 * <h1>该类主要对网关、被直接访问的服务 等接入层使用</h1>
 * 
 * 单独设置一个readiness的接口<br>
 * （单独设置的原因是spring的 /actuator/health
 * 一般用于liveness的检测，readiness的检测如果和liveness合一起时，如果liveness检测失败则容器会被直接销毁，
 * 而readiness检测失败则不会销毁而是流量不会再被路由进来直到恢复健康，所以需要readiness单独配置接口，如果不是接入层则可以都配health）
 * 
 * 网关一旦接收到shutdown命令或容器触发shutdown等，spring Lifecycle 就对
 * readiness接口调整为响应非200，并阻塞等待超过readiness探测次数的时间（之后新的请求便不再进来），最后等待剩余请求处理完毕就可以下线
 * （ProcessingRequestCountWebFilter负责，此外也可以通过spring gateway支持<br>
 * server.shutdown: graceful，实现自动等待请求处理完） <br>
 * 
 * @author Fangfang.Xu
 *
 */
@Endpoint(id = "readiness", enableByDefault = true)
@Slf4j
public class ReadinessEndpoint implements GracefullyShutdown, ApplicationListener<ApplicationReadyEvent> {

	private volatile boolean closed;

	@Autowired
	private NurseryEndpointProperties nurseryEndpointProperties;

	private ApplicationReadyEvent event;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		this.event = event;
	}

	@ReadOperation
	public ReadinessState readiness() throws IllegalStateException {
		if (closed) {
			throw new IllegalStateException("Server Closed");
		}
		if (event == null) {
			throw new IllegalStateException("Server NotReady");
		}

		return ReadinessState.ACCEPTING_TRAFFIC;
	}

	@Override
	public String shutdownName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void shutdown() {
		if (isClosed()) {
			return;
		}
		closed = true;
		try {
			Long shutdownWaitMs = nurseryEndpointProperties.getReadiness().getShutdownWaitMs();
			log.info("readiness shutdown wait ms:{}", shutdownWaitMs);
			Thread.sleep(shutdownWaitMs);
		} catch (InterruptedException e) {
		}
		log.info("readiness shutdown wait end");
	}

	public boolean isClosed() {
		return closed;
	}

	/**
	 * 优先级最高
	 */
	@Override
	public int shutdownOrder() {
		return Integer.MIN_VALUE;
	}
}
