package io.github.icodegarden.nursery.springboot.cache;

import java.util.concurrent.Callable;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.Nullable;

import io.github.icodegarden.nursery.springboot.cache.properties.NurseryCacheProperties;
import io.github.icodegarden.wing.Cacher;

/**
 * 
 * @author Fangfang.Xu
 *
 */
public class NurseryCache extends AbstractValueAdaptingCache {

	private final Cacher cacher;
	private final NurseryCacheProperties properties;

	public NurseryCache(boolean allowNullValues, Cacher cacher, NurseryCacheProperties properties) {
		super(allowNullValues);
		this.cacher = cacher;
		this.properties = properties;
	}

	@Override
	public String getName() {
		return NurseryCache.class.getSimpleName();
	}

	@Override
	public Object getNativeCache() {
		return cacher;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return cacher.getElseSupplier(key.toString(), () -> {
			try {
				return valueLoader.call();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}, properties.getExpireSeconds());
	}

	@Override
	public void put(Object key, @Nullable Object value) {
		if (value != null) {
			cacher.set(key.toString(), value, properties.getExpireSeconds());
		}
	}

	@Override
	public void evict(Object key) {
		cacher.remove(key.toString());
	}

	@Override
	public void clear() {
		// 不支持
	}

	@Override
	protected Object lookup(Object key) {
		return cacher.get(key.toString());
	}

}
