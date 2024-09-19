package io.github.icodegarden.nursery.springboot.cache;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

import io.github.icodegarden.nursery.springboot.cache.properties.NurseryCacheProperties;
import io.github.icodegarden.wing.Cacher;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class NurseryCacheManager extends AbstractTransactionSupportingCacheManager {

	private final Cacher cacher;
	private final NurseryCacheProperties properties;

	public NurseryCacheManager(Cacher cacher, NurseryCacheProperties properties) {
		super();
		this.cacher = cacher;
		this.properties = properties;
	}

	@Override
	protected Collection<? extends Cache> loadCaches() {
		NurseryCache cache = new NurseryCache(false, cacher, properties);
		return Arrays.asList(cache);
	}

}
