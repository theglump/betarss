package org.betarss.core.business;

import static org.betarss.utils.FilterUtils.appendFilterAnd;

import java.io.IOException;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.exception.FeedFilterException;
import org.betarss.feed.FeedFilter;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.provider.ISearchEngine;
import org.betarss.provider.SearchEngineProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedService implements IFeedService {

	@Autowired
	private SearchEngineProvider searchEngineProvider;

	@Autowired
	private FeedFilter feedFilter;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public Feed getFeed(FeedSearch search) throws IOException, FeedFilterException {
		validate(search);
		Provider provider = getProvider(search);
		ISearchEngine searchEngine = getSearchEngine(provider);
		Feed feed = searchEngine.getFeed(search);
		String providerFilter = getProviderFilter(search, searchEngine);
		String filter = appendFilterAnd(providerFilter, search.filter);
		return feedFilter.filter(feed, filter);
	}

	private String getProviderFilter(FeedSearch search, ISearchEngine searchEngine) {
		return searchEngine.getFilter(getLanguage(search), getQuality(search));
	}

	private ISearchEngine getSearchEngine(Provider provider) {
		return searchEngineProvider.provide(provider);
	}

	private Provider getProvider(FeedSearch search) {
		if (search.languages.size() > 0) {
			return configurationService.getProviders().get(getLanguage(search)).get(0);
		}
		return search.providers.get(0);
	}

	private Language getLanguage(FeedSearch parameter) {
		return parameter.languages.size() > 0 ? parameter.languages.get(0) : null;
	}

	private Quality getQuality(FeedSearch search) {
		return search.qualities.size() > 0 ? search.qualities.get(0) : null;
	}

	private void validate(FeedSearch search) {
	}
}
