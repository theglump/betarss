package org.betarss.core;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class BetaseriesFeedProducer {

	public Feed getFeed(ICrawler crawler, String login) throws IOException {
		Map<String, Feed> showNameToFeeds = Maps.newHashMap();
		List<FeedItem> feedItems = Lists.newArrayList();
		for (String title : getPlanningTitles(login)) {
			Feed feed = getFeed(crawler, showNameToFeeds, title);
			for (FeedItem feedItem : feed.getFeedItems()) {
				if (feedItem.getTitle().startsWith((title))) {
					feedItems.add(feedItem);
				}
			}
		}
		return FeedBuilder.start().withTitle(login + "@betaseries' feed").withFeedItems(feedItems).get();
	}

	private Feed getFeed(ICrawler crawler, Map<String, Feed> showNameToFeeds, String title) throws IOException {
		String showName = title.substring(0, title.length() - 7);
		Feed feed = showNameToFeeds.get(showName);
		if (feed == null) {
			feed = crawler.getFeed(showName, getSeason(title));
			showNameToFeeds.put(showName, feed);
		}
		return feed;
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
