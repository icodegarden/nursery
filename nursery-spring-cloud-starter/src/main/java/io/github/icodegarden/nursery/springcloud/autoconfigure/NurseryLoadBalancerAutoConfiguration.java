package io.github.icodegarden.nursery.springcloud.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@Configuration
public class NurseryLoadBalancerAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(value = "icodegarden.nursery.loadBalancer.flowTag.enabled", havingValue = "true", matchIfMissing = false)
	@ConditionalOnDiscoveryEnabled
	@LoadBalancerClients(defaultConfiguration = NurseryLoadBalancerClientConfiguration.class)
	public class FlowTagLoadBalancerAutoConfiguration {

	}
}