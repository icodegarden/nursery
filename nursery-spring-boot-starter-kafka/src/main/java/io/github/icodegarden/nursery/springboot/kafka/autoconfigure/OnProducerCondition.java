package io.github.icodegarden.nursery.springboot.kafka.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * @author Fangfang.Xu
 */
@Order(Ordered.LOWEST_PRECEDENCE - 20)
class OnProducerCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String property = context.getEnvironment().getProperty("icodegarden.nursery.kafka.producer.keySerializer");
		return new ConditionOutcome(property != null, "");
	}
}