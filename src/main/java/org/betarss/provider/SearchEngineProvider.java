package org.betarss.provider;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.betarss.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class SearchEngineProvider {

	private Map<Provider, ISearchEngine> byProvider = Maps.newHashMap();

	@Autowired
	private CpasbienSearchEngine cpasbienSearchEngine;

	@Autowired
	private EztvSearchEngine eztvSearchEngine;

	@Autowired
	private KickassSearchEngine kickassSearchEngine;

	public ISearchEngine provide(Provider provider) {
		return byProvider.get(provider);
	}

	@PostConstruct
	public void postConstruct() {
		add(cpasbienSearchEngine);
		add(eztvSearchEngine);
		add(kickassSearchEngine);
	}

	@PreDestroy
	public void cleanUp() {
		byProvider.clear();
	}

	private void add(ISearchEngine searchEngine) {
		byProvider.put(searchEngine.getProvider(), searchEngine);
	}

}
