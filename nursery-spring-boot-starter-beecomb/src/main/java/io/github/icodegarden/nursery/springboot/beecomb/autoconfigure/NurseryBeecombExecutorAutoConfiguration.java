package io.github.icodegarden.nursery.springboot.beecomb.autoconfigure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
public class NurseryBeecombExecutorAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

	private BeeCombExecutor beeCombExecutor;

	@Autowired(required = false)
	private List<JobHandler> jobHandlers;

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.beecomb.executor.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public BeeCombExecutor beeCombExecutor(NurseryBeeCombExecutorProperties properties, Environment env) {
		log.info("nursery init bean of BeeCombExecutor");
		
		String executorName = properties.getExecutorName();
		if(!StringUtils.hasText(executorName)) {
			String appName = env.getProperty("spring.application.name");
			Assert.hasText(appName, "spring.application.name must config when executorName not config.");
			
			executorName = appName;
		}

		BeeCombExecutor beeCombExecutor = BeeCombExecutor.start(executorName, properties);

		this.beeCombExecutor = beeCombExecutor;

		return beeCombExecutor;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (!CollectionUtils.isEmpty(jobHandlers)) {
			/*
			 * 在服务可用后才注册
			 */
			beeCombExecutor.registerReplace(jobHandlers);
		}
	}

}
