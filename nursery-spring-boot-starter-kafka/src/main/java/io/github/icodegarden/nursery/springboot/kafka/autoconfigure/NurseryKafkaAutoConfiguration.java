package io.github.icodegarden.nursery.springboot.kafka.autoconfigure;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.icodegarden.nursery.springboot.kafka.properties.NurseryKafkaProperties;
import io.github.icodegarden.nutrient.kafka.reliability.ReliabilityProducer;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConditionalOnClass(ReliabilityProducer.class)
@EnableConfigurationProperties({ NurseryKafkaProperties.class })
@Configuration
@Slf4j
public class NurseryKafkaAutoConfiguration {

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.kafka.reliability.producer.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public ReliabilityProducer reliabilityProducer(NurseryKafkaProperties commonsKafkaProperties) {
		log.info("nursery init bean of ReliabilityProducer");

		commonsKafkaProperties.validate();

		NurseryKafkaProperties.Producer producer = commonsKafkaProperties.getProducer();

		Properties props = new Properties();
		props.put("bootstrap.servers", commonsKafkaProperties.getBootstrapServers());
		props.put("key.serializer", producer.getKeySerializer());
		props.put("value.serializer", producer.getValueSerializer());
		props.putAll(producer.getProps());
		return new ReliabilityProducer<>(props);
	}
	
	/**
	 * 适用于单元测试，因为单元测试一般无需真实的生产消息
	 */
	@ConditionalOnClass(name = { "org.junit.jupiter.api.Test" })
	@ConditionalOnProperty(value = "icodegarden.nursery.kafka.reliability.producer.noOp.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public ReliabilityProducer reliabilityProducer4Test(NurseryKafkaProperties commonsKafkaProperties) {
		log.info("nursery init bean of reliabilityProducer4Test");

		return new ReliabilityProducer((KafkaProducer) null) {
			public org.apache.kafka.clients.producer.RecordMetadata sendSync(
					org.apache.kafka.clients.producer.ProducerRecord record) {
				return null;
			}
		};
	}
}
