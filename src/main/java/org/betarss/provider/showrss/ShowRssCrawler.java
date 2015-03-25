package org.betarss.provider.showrss;

import java.io.IOException;
import java.util.List;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.betarss.provider.Crawler;
import org.betarss.utils.RssParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShowRssCrawler implements Crawler {

	private static final String SEARCH_URL = "http://showrss.info/feeds/";

	@Autowired
	private ShowRssCache showRssCache;

	@Override
	public List<Torrent<ShowEpisode>> doCrawl(String show, Integer season) throws IOException, FeedFilterException {
		if (season != null) {
			// LOG.warn("season submitted, the crawler only fetch whole season");
		}
		return new RssParser(SEARCH_URL + showRssCache.get(show) + ".rss").readFeed();
	}

}