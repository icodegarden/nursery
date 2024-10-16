package io.github.icodegarden.nursery.servlet.web.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import io.github.icodegarden.nutrient.kafka.UnRetryableException;
import io.github.icodegarden.nutrient.kafka.reliability.ReliabilityHandler;

/**
 * @author Fangfang.Xu
 */
@Component
public class DemoReliabilityHandler implements ReliabilityHandler{

	@Override
	public boolean handle(ConsumerRecord record) throws UnRetryableException {
		System.out.println(record);
		return true;
	}

	@Override
	public boolean primaryStore(ConsumerRecord failedRecord, Throwable handleCause) throws UnRetryableException {
		return false;
	}

	@Override
	public boolean secondaryStore(ConsumerRecord failedRecord, Throwable primaryStoreCause)
			throws UnRetryableException {
		return false;
	}

	@Override
	public void onStoreFailed(ConsumerRecord failedRecord, Throwable secondaryStoreCause) {
		
	}

}
