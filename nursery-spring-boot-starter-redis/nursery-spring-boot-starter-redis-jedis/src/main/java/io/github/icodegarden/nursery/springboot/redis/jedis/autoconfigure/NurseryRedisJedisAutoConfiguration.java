package io.github.icodegarden.nursery.springboot.redis.jedis.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springboot.redis.jedis.JedisRedisExecutorBuilder;
import io.github.icodegarden.nursery.springboot.redis.properties.NurseryRedisProperties;
import io.github.icodegarden.nutrient.redis.RedisExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(RedisExecutor.class)
@EnableConfigurationProperties({ NurseryRedisProperties.class })
@Configuration
@Slf4j
public class NurseryRedisJedisAutoConfiguration {

	@ConditionalOnProperty(value = "icodegarden.nursery.redis.executor.enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public RedisExecutor redisExecutor(NurseryRedisProperties redisProperties) {
		log.info("nursery init bean of RedisExecutor");
		return JedisRedisExecutorBuilder.create(redisProperties);
	}

}
