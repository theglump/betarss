package org.betarss.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.BetarssSearch;
import org.betarss.domain.BetaseriesSearch;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.exception.FeedFilterException;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Procedure;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

@Service
public class BetaseriesService {

	private static final String BETASERIES_FEED_URL = "https://www.betaseries.com/rss/planning/";
	private static final Pattern SEASON_PATTERN = Pattern.compile(".*S0?(\\d+).*");

	@Autowired
	private BetarssService betarssService;

	public Feed getFeed(final BetaseriesSearch search) throws IOException, FeedFilterException {
		final List<FeedItem> feedItems = new CopyOnWriteArrayList<FeedItem>();

		SetMultimap<String, String> titlesBySeason = HashMultimap.create();
		for (String title : getItemTitles(search.login)) {
			String key = title.substring(0, title.length() - 4);
			titlesBySeason.put(key, title);
		}

		List<Procedure> procedures = Lists.newArrayList();
		for (String key : titlesBySeason.keys()) {
			final Set<String> titles = titlesBySeason.get(key);

			procedures.add(new Procedure() {

				@Override
				public void doCall() throws Exception {
					String first = titles.iterator().next();
					String showName = first.substring(0, first.length() - 7);
					BetarssSearch betarssSearch = new BetarssSearch(search);
					betarssSearch.show = showName;
					betarssSearch.season = getSeason(first);
					Feed feed = betarssService.search(betarssSearch);
					for (String title : titles) {
						for (FeedItem feedItem : feed.getFeedItems()) {
							if (feedItem.getTitle().startsWith((title))) {
								feedItems.add(feedItem);
							}
						}
					}
				}
			});
		}
		BetarssUtils.multiThreadCalls(procedures, 120);
		return FeedBuilder.start().withTitle(search.login + "@betaseries' feed").withFeedItems(feedItems).get();
	}

	private List<String> getItemTitles(String login) throws IOException {
		List<String> titles = new ArrayList<String>();
		Elements elementsByTag = Jsoup.connect(BETASERIES_FEED_URL + login).get().getElementsByTag("title");
		Iterator<org.jsoup.nodes.Element> iterator = elementsByTag.iterator();
		iterator.next(); // skip global title
		while (iterator.hasNext()) {
			String rawTitle = iterator.next().text();
			titles.add(rawTitle.substring(9, rawTitle.length() - 3));
		}
		return titles;
	}

	private int getSeason(String str) {
		Matcher m = SEASON_PATTERN.matcher(str);
		return m.find() ? Integer.parseInt(m.group(1)) : 0;
	}
}
