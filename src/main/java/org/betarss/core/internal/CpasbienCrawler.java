package org.betarss.core.internal;

import static org.betarss.utils.ShowUtils.upperCaseString;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.core.ICrawler;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.domain.FeedItemBuilder;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class CpasbienCrawler implements ICrawler {

	private static final Pattern EPISODE_ITEM_PATTERN = Pattern.compile("(.*dl-torrent.*/(.*)\\.html.*(\\d+/\\d+/\\d+).*>(.*)<.*)",
			Pattern.CASE_INSENSITIVE);

	private static final int TORRENT_NAME = 2;
	private static final int DATE = 3;
	private static final int TITLE = 4;

	@Override
	public Feed getFeed(String showName, int season) throws IOException {
		return FeedBuilder //
				.start() //
				.withTitle(upperCaseString(showName) + " " + ShowUtils.getFormattedShowSeason(season)) //
				.withFeedItems(getFeed(fetchHtml(showName, season))) //
				.get();
	}

	private List<FeedItem> getFeed(String html) throws IOException {
		List<FeedItem> feedItems = Lists.newArrayList();
		Matcher m = EPISODE_ITEM_PATTERN.matcher(html);
		while (m.find()) {
			FeedItem feedItem = createFeed(m);
			feedItems.add(feedItem);
		}
		return feedItems;
	}

	private FeedItem createFeed(Matcher m) {
		return FeedItemBuilder.start(). //
				withTitle(m.group(TITLE)). //
				withDescription(m.group(TITLE)). //
				withLocation(getLocation(m.group(TORRENT_NAME))). //
				withDate(parseDate(m.group(DATE))). //
				get();
	}

	private String fetchHtml(String showName, int season) throws IOException {
		return Jsoup //
				.connect("http://www.cpasbien.pw/recherche/") //
				.userAgent("Mozilla") //
				.data("champ_recherche", showName + " " + ShowUtils.getFormattedShowSeason(season)) //
				.post() //
				.html();
	}

	private Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (java.text.ParseException e) {
			return new Date();
		}
	}

	private String getLocation(String torrentName) {
		return "http://www.cpasbien.pw/telechargement/" + torrentName + ".torrent";
	}

}
