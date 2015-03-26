package org.betarss.infrastructure.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

	private long lastRefreshedAt;
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
		lastRefreshedAt = System.currentTimeMillis();
		cache = Maps.newConcurrentMap();
		init();
	}

	protected void init() {
	}

	protected boolean lazy() {
		return false;
	}

	/**
	 * @return how many hours cache must be refresh
	 */
	protected long refreshEvery() {
		return 0;
	}

	private boolean needRefresh() {
		if (refreshEvery() <= 0) {
			return false;
		}
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - lastRefreshedAt;
		long elapsedMinutes = TimeUnit.MINUTES.convert(elapsedTime, TimeUnit.HOURS);
		return elapsedMinutes >= refreshEvery();
	}

}
