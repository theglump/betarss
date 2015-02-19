package org.betarss.core.internal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.core.ICrawler;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class EztvCrawler implements ICrawler {

	// href="/torrent/cfg2s6m" title="Better Call Saul S01E03 720p HDTV X264-DIMENSION [eztv]">Better Call Saul S01E03 720p HDTV X264-DIMENSION [eztv]</a>	

	private static final Pattern EPISODE_ITEM_PATTERN = Pattern.compile("href=\"\\/torrent\\/(.*)\" title=\"(.*)\">", Pattern.CASE_INSENSITIVE);

	@Override
	public Feed getFeed(String showName, int season) throws IOException {
		return FeedBuilder.start().withFeedItems(getFeed(fetchHtml(showName, season))).get();
	}

	private List<FeedItem> getFeed(String html) throws IOException {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		Matcher m = EPISODE_ITEM_PATTERN.matcher(html);
		while (m.find()) {
			FeedItem feedItem = createFeed(m);
			feedItems.add(feedItem);
		}
		return feedItems;
	}

	private FeedItem createFeed(Matcher m) {
		//		return FeedItemBuilder.start(). //
		//				withTitle(m.group(TITLE)). //
		//				withDescription(m.group(TITLE)). //
		//				withLocation(getLocation(m.group(TORRENT_NAME))). //
		//				withDate(parseDate(m.group(DATE))). //
		//				get();
		return null;
	}

	private String fetchHtml(String showName, int season) throws IOException {
		String url = ("http://rarbg.com/torrents.php?search=" + showName + " " + ShowUtils.getFormattedShowSeason(season) + " eztv").replaceAll(" ",
				"+");
		return Jsoup //
				.connect(url) //
				.userAgent("Chrome") //
				.get() //
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
