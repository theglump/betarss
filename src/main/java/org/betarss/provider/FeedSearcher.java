package org.betarss.provider;

import static org.betarss.utils.FilterUtils.joinFilterAnd;

import java.io.IOException;

import org.betarss.domain.BetarssSearch;
import org.betarss.domain.Feed;
import org.betarss.domain.Language;
import org.betarss.domain.Quality;
import org.betarss.exception.FeedFilterException;
import org.betarss.feed.FeedFilterer;

// TODO : to prototype ?
public class FeedSearcher {

	private FeedFilterer feedFilterer;

	private ICrawler crawler;

	private IFilterComputor filterComputor;

	public FeedSearcher(FeedFilterer feedFilterer, ICrawler crawler, IFilterComputor filterComputor) {
		this.feedFilterer = feedFilterer;
		this.crawler = crawler;
		this.filterComputor = filterComputor;
	}

	public Feed search(BetarssSearch search) throws IOException, FeedFilterException {
		Feed feed = crawler.getFeed(search);
		String filter = computeFilter(search);
		return feedFilterer.filter(feed, filter);
	}

	private String computeFilter(BetarssSearch search) {
		String searchFilter = filterComputor.getFilter(getLanguage(search), getQuality(search));
		return joinFilterAnd(searchFilter, search.filter);
	}

	private Language getLanguage(BetarssSearch parameter) {
		return parameter.languages.size() > 0 ? parameter.languages.get(0) : null;
	}

	private Quality getQuality(BetarssSearch search) {
		return search.qualities.size() > 0 ? search.qualities.get(0) : null;
	}

}
