package org.betarss.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BetaseriesFeedProducer {

	private ICrawler crawler;

	public BetaseriesFeedProducer(ICrawler crawler) {
		this.crawler = crawler;
	}

	public Feed getFeed(String login) throws IOException {
		Map<String, Feed> feeds = new HashMap<String, Feed>();
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		for (String title : getPlanningTitles(login)) {
			String showName = title.substring(0, title.length() - 7);
			Feed feed = feeds.get(showName);
			if (feed == null) {
				feed = crawler.getFeed(showName, getSeason(title));
				feeds.put(showName, feed);
			}
			for (FeedItem feedItem : feed.getFeedItems()) {
				if (feedItem.getTitle().startsWith((title))) {
					feedItems.add(feedItem);
					break;
				}
			}
		}
		return FeedBuilder.start().withFeedItems(feedItems).get();
	}

	private List<String> getPlanningTitles(String login) throws IOException {
		List<String> titles = new ArrayList<String>();
		Elements elementsByTag = Jsoup.connect("https://www.betaseries.com/rss/planning/" + login).get().getElementsByTag("title");
		Iterator<Element> iterator = elementsByTag.iterator();
		iterator.next(); // skip global title
		while (iterator.hasNext()) {
			String rawTitle = iterator.next().text();
			titles.add(rawTitle.substring(9, rawTitle.length() - 3));
		}
		return titles;

	}

	private int getSeason(String str) {
		Matcher m = Pattern.compile(".*S0?(\\d+).*").matcher(str);
		return m.find() ? Integer.parseInt(m.group(1)) : 0;
	}
}
