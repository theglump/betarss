package org.betarss.provider.showrss;

import java.util.List;

import org.betarss.domain.Torrent;
import org.betarss.infrastructure.http.HttpClient;
import org.betarss.provider.Crawler;
import org.betarss.utils.RssParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ShowRssCrawler implements Crawler {

	@Autowired
	private ShowRssCache showRssCache;

	@Autowired
	@Qualifier("httpClient")
	private HttpClient HttpClient;

	@Override
	public List<Torrent> doCrawl(String show, Integer season) {
		return new RssParser(getFeed(show)).parseShowRss();
	}

	private String getFeed(String show) {
		String url = getFeedUrl(show);
		return HttpClient.dataAsString(url);
	}

	private String getFeedUrl(String show) {
		return "http://showrss.info/feeds/" + showRssCache.get(show) + ".rss";
	}

}