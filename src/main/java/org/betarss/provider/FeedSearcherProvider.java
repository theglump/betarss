package org.betarss.provider;

import org.betarss.domain.Provider;
import org.betarss.provider.cpasbien.CpasbienCrawler;
import org.betarss.provider.cpasbien.CpasbienFilterComputor;
import org.betarss.provider.eztv.EztvCrawler;
import org.betarss.provider.kickass.KickassCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedSearcherProvider {

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
	private DefaultFilterComputor defaultFilterComputor;

	public TorrentSearcher get(Provider provider) {
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

	private TorrentSearcher createCpasbienFeedSearcher() {
		return new TorrentSearcher(torrentFilterer, cpasbienCrawler, cpasbienFilterComputor);
	}

	private TorrentSearcher createEztvFeedSearcher() {
		return new TorrentSearcher(torrentFilterer, eztvCrawler, defaultFilterComputor);
	}

	private TorrentSearcher createKickAssFeedSearcher() {
		return new TorrentSearcher(torrentFilterer, kickassCrawler, defaultFilterComputor);
	}

}
