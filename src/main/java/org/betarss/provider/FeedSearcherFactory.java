package org.betarss.provider;

import org.betarss.domain.Provider;
import org.betarss.feed.FeedFilterer;
import org.betarss.provider.cpasbien.CpasbienCrawler;
import org.betarss.provider.cpasbien.CpasbienFilterComputor;
import org.betarss.provider.eztv.EztvCrawler;
import org.betarss.provider.kickass.KickassCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedSearcherFactory {

	@Autowired
	private FeedFilterer feedFilterer;

	@Autowired
	private CpasbienCrawler cpasbienCrawler;

	@Autowired
	private CpasbienFilterComputor cpasbienFilterComputor;

	@Autowired
	private EztvCrawler eztvCrawler;

	@Autowired
	private KickassCrawler kickassCrawler;

	@Autowired
	private DefaultFilterComputor defaultFilterComputor;

	public FeedSearcher get(Provider provider) {
		switch (provider) {
		case EZTV:
			return createEztvFeedSearcher();
		case KICKASS:
			return createKickAssFeedSearcher();
		case CPASBIEN:
			return createCpasbienFeedSearcher();
		}
		throw new UnsupportedOperationException("Provider " + provider + " is not supported");
	}

	private FeedSearcher createCpasbienFeedSearcher() {
		return new FeedSearcher(feedFilterer, cpasbienCrawler, cpasbienFilterComputor);
	}

	private FeedSearcher createEztvFeedSearcher() {
		return new FeedSearcher(feedFilterer, eztvCrawler, defaultFilterComputor);
	}

	private FeedSearcher createKickAssFeedSearcher() {
		return new FeedSearcher(feedFilterer, kickassCrawler, defaultFilterComputor);
	}

}
