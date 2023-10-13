package io.github.icodegarden.nursery.springcloud.gateway.spi.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.github.icodegarden.nursery.springcloud.gateway.core.security.signature.App;
import io.github.icodegarden.nursery.springcloud.gateway.properties.NurseryGatewaySecurityProperties;
import io.github.icodegarden.nursery.springcloud.gateway.spi.AppProvider;
import io.github.icodegarden.nutrient.lang.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 从配置中获取的
 * 
 * @author Fangfang.Xu
 *
 */
@Slf4j
public class ConfiguredAppProvider implements AppProvider {

	private final NurseryGatewaySecurityProperties securityProperties;

	private Map<String/* appId */, App> appMap = new HashMap<String, App>();

	public ConfiguredAppProvider(NurseryGatewaySecurityProperties securityProperties) {
		this.securityProperties = securityProperties;

		init();
	}

	private void init() {
		ScheduledThreadPoolExecutor scheduledThreadPool = ThreadUtils
				.newSingleScheduledThreadPool(this.getClass().getSimpleName());
		scheduledThreadPool.scheduleWithFixedDelay(() -> {
			try {
				refreshApps();
			} catch (Exception e) {
				log.error("ex on refreshApps", e);
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

	@Override
	public App getApp(String appId) {
		return appMap.get(appId);
	}

	private void refreshApps() {
		NurseryGatewaySecurityProperties.Signature signature = securityProperties.getSignature();
		if (signature != null) {
			List<App> apps = signature.getApps();
			if (apps != null) {
				this.appMap = apps.stream().collect(Collectors.toMap(App::getAppId, app -> app));
			}
		}
	}
}
