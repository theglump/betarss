package org.betarss.provider.showrss;

import java.util.List;

import org.betarss.domain.Torrent;
import org.betarss.infrastructure.HttpService;
import org.betarss.provider.Crawler;
import org.betarss.utils.RssParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShowRssCrawler implements Crawler {

	@Autowired
	private ShowRssCache showRssCache;

	@Autowired
	private HttpService HttpService;

	@Override
	public List<Torrent> doCrawl(String show, Integer season) {
		return new RssParser(getFeed(show)).parseShowRss();
	}

	private String getFeed(String show) {
		String url = getFeedUrl(show);
		return HttpService.dataAsString(url);
	}

	private String getFeedUrl(String show) {
		return "http://showrss.info/feeds/" + showRssCache.get(show) + ".rss";
	}

}