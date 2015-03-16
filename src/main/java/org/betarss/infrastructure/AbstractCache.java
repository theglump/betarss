package org.betarss.infrastructure;

import java.util.Map;

import com.google.common.collect.Maps;

public abstract class AbstractCache<K, V> implements ICache<K, V> {

	Map<K, V> cache;

	public AbstractCache() {
		if (!lazy()) {
			internalInit();
		}
	}

	@Override
	public V get(K key) {
		if (cache == null && lazy()) {
			internalInit();
		}
		return cache.get(key);
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	private void internalInit() {
		cache = Maps.newConcurrentMap();
		init();
	}

	protected void init() {
	}

	protected boolean lazy() {
		return false;
	}

}
