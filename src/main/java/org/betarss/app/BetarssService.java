package org.betarss.app;

import java.io.IOException;

import org.betarss.domain.BetarssSearch;
import org.betarss.domain.Feed;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.exception.FeedFilterException;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.provider.FeedSearcherProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BetarssService {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FeedSearcherProvider feedSearcherProvider;

	public Feed search(BetarssSearch search) throws IOException, FeedFilterException {
		return feedSearcherProvider.get(getProvider(search)).search(search);
	}

	private Provider getProvider(BetarssSearch search) {
		if (search.languages.size() > 0) {
			return configurationService.getProviders().get(getLanguage(search)).get(0);
		}
		return search.providers.get(0);
	}

	private Language getLanguage(BetarssSearch parameter) {
		return parameter.languages.size() > 0 ? parameter.languages.get(0) : null;
	}

}
