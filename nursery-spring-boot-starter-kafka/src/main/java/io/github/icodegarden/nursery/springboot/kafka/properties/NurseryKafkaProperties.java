package io.github.icodegarden.nursery.springboot.kafka.properties;

import java.util.List;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import io.github.icodegarden.nutrient.lang.Validateable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Fangfang.Xu
 *
 */
@ConfigurationProperties(prefix = "icodegarden.nursery.kafka")
@Getter
@Setter
@ToString
public class NurseryKafkaProperties implements Validateable {

	@NonNull
	private String bootstrapServers;// 172.22.122.27:9092,172.22.122.28:9092
	private Producer producer;
	private List<Consumer> consumers;

	@Override
	public void validate() throws IllegalArgumentException {
		Assert.hasText(bootstrapServers, "bootstrapServers must not empty");
//		Assert.notNull(producer, "producer must not null");
	}

	@Getter
	@Setter
	@ToString
	public static class Producer implements Validateable {
		@NonNull
		private String keySerializer;// org.apache.kafka.common.serialization.StringSerializer
		@NonNull
		private String valueSerializer;// org.apache.kafka.common.serialization.StringSerializer

		/**
		 * 其他kafka props
		 */
		private Properties props = new Properties();

		@Override
		public void validate() throws IllegalArgumentException {
			Assert.hasText(keySerializer, "keySerializer must not empty");
			Assert.hasText(valueSerializer, "valueSerializer must not empty");
		}
	}

	@Getter
	@Setter
	@ToString
	public static class Consumer implements Validateable {
		@NonNull
		private String keyDeserializer;//org.apache.kafka.common.serialization.StringDeserializer
		@NonNull
		private String valueDeserializer;//org.apache.kafka.common.serialization.StringDeserializer
		@NonNull
		private String groupId;
		@NonNull
		private List<String> topics;
		
		private String clientId;
		
		@NonNull
		private String reliabilityHandlerBeanName;
		
		/**
		 * 其他kafka props
		 */
		private Properties props = new Properties();

		@Override
		public void validate() throws IllegalArgumentException {
			Assert.hasText(keyDeserializer, "keyDeserializer must not empty");
			Assert.hasText(valueDeserializer, "valueDeserializer must not empty");
			Assert.hasText(groupId, "groupId must not empty");
			Assert.notEmpty(topics, "topics must not empty");
			Assert.hasText(reliabilityHandlerBeanName, "reliabilityHandlerBeanName must not empty");
		}
	}
}