package org.betarss.infrastructure.cache;

import static java.lang.System.currentTimeMillis;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

	private long lastRefreshedAt;
	private Map<K, V> cache;

	private final Object mutex;

	public AbstractCache() {
		mutex = this;
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
		if (refreshEvery() > 0) {
			setLastRefreshedAt(System.currentTimeMillis());
		}
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
		long elapsedMillis = currentTimeMillis() - getLastRefreshedAt();
		long elapsedMinutes = TimeUnit.MINUTES.convert(elapsedMillis, TimeUnit.HOURS);
		return elapsedMinutes >= refreshEvery();
	}

	// Fix : Those mutators should be in a dedicated class
	public long getLastRefreshedAt() {
		synchronized (mutex) {
			return lastRefreshedAt;
		}
	}

	public void setLastRefreshedAt(long lastRefreshedAt) {
		synchronized (mutex) {
			this.lastRefreshedAt = lastRefreshedAt;
		}
	}

}
