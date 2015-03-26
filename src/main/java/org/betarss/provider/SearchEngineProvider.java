package org.betarss.provider;

import org.betarss.domain.Provider;
import org.betarss.provider.cpasbien.CpasbienCrawler;
import org.betarss.provider.cpasbien.CpasbienFilterComputor;
import org.betarss.provider.eztv.EztvCrawler;
import org.betarss.provider.kickass.KickassCrawler;
import org.betarss.provider.showrss.ShowRssCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchEngineProvider {

	@Autowired
	private TorrentFilterer torrentFilterer;

	@Autowired
	private CpasbienCrawler cpasbienCrawler;

	@Autowired
	private CpasbienFilterComputor cpasbienFilterComputor;

	@Autowired
	private EztvCrawler eztvCrawler;

	@Autowired
	private KickassCrawler kickassCrawler;

	@Autowired
	private ShowRssCrawler showRssCrawler;

	@Autowired
	private DefaultFilterComputor defaultFilterComputor;

	public SearchEngine get(Provider provider) {
		switch (provider) {
		case EZTV:
			return createEztvFeedSearcher();
		case KICKASS:
			return createKickAssFeedSearcher();
		case CPASBIEN:
			return createCpasbienFeedSearcher();
		case SHOWRSS:
			return createShowRssFeedSearcher();
		}
		throw new IllegalArgumentException("Provider " + provider + " is not supported");
	}

	private SearchEngine createCpasbienFeedSearcher() {
		return new SearchEngine(torrentFilterer, cpasbienCrawler, cpasbienFilterComputor);
	}

	private SearchEngine createEztvFeedSearcher() {
		return new SearchEngine(torrentFilterer, eztvCrawler, defaultFilterComputor);
	}

	private SearchEngine createKickAssFeedSearcher() {
		return new SearchEngine(torrentFilterer, kickassCrawler, defaultFilterComputor);
	}

	private SearchEngine createShowRssFeedSearcher() {
		return new SearchEngine(torrentFilterer, showRssCrawler, defaultFilterComputor);
	}

}
