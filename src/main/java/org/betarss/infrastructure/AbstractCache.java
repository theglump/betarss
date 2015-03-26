package org.betarss.infrastructure;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

	private long refreshedAt;
	private Map<K, V> cache;

	public AbstractCache() {
		if (!lazy()) {
			internalInit();
		}
	}

	@Override
	public V get(K key) {
		if (needRefresh() || (cache == null && lazy())) {
			internalInit();
		}
		return cache.get(key);
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	private void internalInit() {
		refreshedAt = System.currentTimeMillis();
		cache = Maps.newConcurrentMap();
		init();
	}

	protected void init() {
	}

	protected boolean lazy() {
		return false;
	}

	protected boolean needRefresh() {
		return false;
	}

	protected long timeSinceRefresh(TimeUnit timeUnit) {
		long currentTime = System.currentTimeMillis();
		return timeUnit.convert(currentTime - refreshedAt, timeUnit);
	}

}
