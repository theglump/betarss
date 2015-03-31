package org.betarss.provider.showrss;

import java.util.List;

import org.betarss.domain.Torrent;
import org.betarss.infrastructure.http.HttpClient;
import org.betarss.provider.Crawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ShowRssCrawler implements Crawler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowRssCrawler.class);

	@Autowired
	private ShowRssCache showRssCache;

	@Autowired
	@Qualifier("httpClient")
	private HttpClient HttpClient;

	@Override
	public List<Torrent> doCrawl(String show, Integer season) {
		String feed = getFeed(show);
		return ShowRssFeedParser.parse(feed);
	}

	private String getFeed(String show) {
		Integer showRssId = showRssCache.get(show);
		if (showRssId == null) {
			LOGGER.error("Could not find showrss id for show : " + show);
			return "";
		}
		String url = "http://showrss.info/feeds/" + showRssId + ".rss";
		return HttpClient.dataAsString(url);
	}

}