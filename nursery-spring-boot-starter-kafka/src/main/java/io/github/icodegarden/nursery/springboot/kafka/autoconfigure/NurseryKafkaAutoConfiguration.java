package io.github.icodegarden.nursery.springboot.kafka.autoconfigure;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.github.icodegarden.nursery.springboot.kafka.properties.NurseryKafkaProperties;
import io.github.icodegarden.nutrient.kafka.reliability.ReliabilityConsumer;
import io.github.icodegarden.nutrient.kafka.reliability.ReliabilityHandler;
import io.github.icodegarden.nutrient.kafka.reliability.ReliabilityProducer;
import io.github.icodegarden.nutrient.lang.util.ThreadUtils;
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
public class NurseryKafkaAutoConfiguration implements ApplicationContextAware {

	private ApplicationContext ac;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ac = applicationContext;
	}

	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "icodegarden.nursery.kafka.reliability.producer.enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnProducer
	@Bean
	public ReliabilityProducer reliabilityProducer(NurseryKafkaProperties kafkaProperties) {
		log.info("nursery init bean of ReliabilityProducer");
		kafkaProperties.validate();

		NurseryKafkaProperties.Producer producer = kafkaProperties.getProducer();
		producer.validate();

		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaProperties.getBootstrapServers());
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

	@ConditionalOnProperty(value = "icodegarden.nursery.kafka.reliability.consumer.enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public ReliabilityConsumers reliabilityConsumers(NurseryKafkaProperties kafkaProperties,
			List<ReliabilityHandler> reliabilityHandlers) {
		log.info("nursery init bean of ReliabilityConsumer");
		kafkaProperties.validate();

		List<NurseryKafkaProperties.Consumer> consumerProps = kafkaProperties.getConsumers();
		if (CollectionUtils.isEmpty(consumerProps)) {
			return new ReliabilityConsumers(null);
		}

		List<ReliabilityConsumer> list = consumerProps.stream().map(consumerProp -> {
			consumerProp.validate();

			Properties props = new Properties();
			props.put("bootstrap.servers", kafkaProperties.getBootstrapServers());
			props.put("key.deserializer", consumerProp.getKeyDeserializer());
			props.put("value.deserializer", consumerProp.getValueDeserializer());
			props.put("group.id", consumerProp.getGroupId());
			if (StringUtils.hasText(consumerProp.getClientId())) {
				props.put("client.id", consumerProp.getClientId());//
			}
			props.putAll(consumerProp.getProps());

			ReliabilityHandler reliabilityHandler;
			try {
				reliabilityHandler = (ReliabilityHandler) ac.getBean(consumerProp.getReliabilityHandlerBeanName());
			} catch (NoSuchBeanDefinitionException e) {
				throw new IllegalArgumentException("ReliabilityHandler bean name of "
						+ consumerProp.getReliabilityHandlerBeanName() + " Not Exist.", e);
			}

			ReliabilityConsumer consumer = new ReliabilityConsumer(props, reliabilityHandler);
			consumer.subscribe(consumerProp.getTopics());
			return consumer;
		}).collect(Collectors.toList());

		return new ReliabilityConsumers(list).start();
	}

	private class ReliabilityConsumers implements Closeable {

		private final List<ReliabilityConsumer> consumers;
		private ThreadPoolExecutor threadPool;

		public ReliabilityConsumers(List<ReliabilityConsumer> consumers) {
			super();
			this.consumers = consumers;
		}

		public ReliabilityConsumers start() {
			if (consumers != null) {
				threadPool = ThreadUtils.newEagerThreadPool(0, consumers.size(), 0, 0, "reliabilityConsumer");

				consumers.forEach(reliabilityConsumer -> {
					threadPool.execute(() -> reliabilityConsumer.consume(100));
				});
			}
			return this;
		}

		@Override
		public void close() throws IOException {
			if (consumers != null) {
				consumers.forEach(ReliabilityConsumer::close);
			}

			if (threadPool != null) {
				threadPool.shutdown();
			}
		}

	}
}
