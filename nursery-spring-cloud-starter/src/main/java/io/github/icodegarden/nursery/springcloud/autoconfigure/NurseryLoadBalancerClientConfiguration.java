package io.github.icodegarden.nursery.springcloud.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import io.github.icodegarden.nursery.springcloud.loadbalancer.FlowTagLoadBalancer;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class NurseryLoadBalancerClientConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public ReactorLoadBalancer<ServiceInstance> flowTagLoadBalancer(Environment environment,
			LoadBalancerClientFactory loadBalancerClientFactory) {
		String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
		return new FlowTagLoadBalancer(
				loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
	}

}