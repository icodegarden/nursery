package io.github.icodegarden.nursery.springboot.beecomb.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.github.icodegarden.beecomb.executor.BeeCombExecutor;
import io.github.icodegarden.beecomb.executor.registry.JobHandler;
import io.github.icodegarden.nursery.springboot.beecomb.properties.NurseryBeeCombExecutorProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(BeeCombExecutor.class)
@EnableConfigurationProperties({ NurseryBeeCombExecutorProperties.class })
@Configuration
@Slf4j
public class NurseryBeecombExecutorAutoConfiguration {

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.beecomb.executor.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public BeeCombExecutor beeCombExecutor(NurseryBeeCombExecutorProperties properties, List<JobHandler> jobHandlers,
			Environment env) {
		log.info("nursery init bean of BeeCombExecutor");

		String appName = env.getProperty("spring.application.name");
		Assert.hasText(appName, "spring.application.name must config");

		BeeCombExecutor beeCombExecutor = BeeCombExecutor.start(appName, properties);
		if (!CollectionUtils.isEmpty(jobHandlers)) {
			beeCombExecutor.registerReplace(jobHandlers);
		}

		return beeCombExecutor;
	}

}
