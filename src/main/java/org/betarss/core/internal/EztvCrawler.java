package org.betarss.core.internal;

import static org.betarss.utils.ShowUtils.getFormattedShowSeason;
import static org.betarss.utils.Utils.doTry;
import static org.jsoup.Jsoup.connect;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.betarss.core.ICrawler;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.domain.FeedItemBuilder;
import org.betarss.utils.ShowUtils;
import org.betarss.utils.Utils.Try;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class EztvCrawler implements ICrawler {

	private static final String SEARCH_URL = "https://eztv.ch/search/";

	private static final String PATTERN_1 = "(Added on: <b>(\\d+, \\w+, \\d+)</b>)|(title=\"(";
	private static final String PATTERN_2_START = "((?!\").)*MB\\))\"((?!forum_thread_post_end).)*";
	private static final String PATTERN_2_MAGNET = PATTERN_2_START + "<a href=\"(magnet((?!\").)*)\"((?!magnet).)*)";
	private static final String PATTERN_2_TORRENT = PATTERN_2_START + "<a href=\"((((?!\").)*))\" class=\"download_1\"((?!download_1).)*)";

	private static final boolean MAGNET = false;
	private static final int FETCH_HTML_RETRY_NUMBER = 10;

	private static final int DATE = 2;
	private static final int TITLE = 4;
	private static final int LOCATION = 7;

	private static final Map<String, Integer> TV_SHOW_IDS = Maps.newConcurrentMap();

	@Override
	public Feed getFeed(final String showName, int season) throws IOException {
		List<FeedItem> feedItems = getFeed(doTry(FETCH_HTML_RETRY_NUMBER, new Try<String>() {

			@Override
			public String doTry() throws Exception {
				return connect(SEARCH_URL).userAgent("Mozilla/5.0").data("SearchString", getTvShowId(showName).toString()).post().html();
			}

		}), getEntryPattern(showName + " " + getFormattedShowSeason(season)));
		return FeedBuilder.start().withTitle(computeTitle(showName, season)).withFeedItems(feedItems).get();
	}

	@Override
	public Feed getFeed() throws IOException {
		List<FeedItem> feedItems = getFeed(doTry(FETCH_HTML_RETRY_NUMBER, new Try<String>() {

			@Override
			public String doTry() throws Exception {
				return connect(SEARCH_URL).userAgent("Mozilla/5.0").post().html();
			}

		}), getEntryPattern(""));
		return FeedBuilder.start().withTitle(null).withFeedItems(feedItems).get();
	}

	private List<FeedItem> getFeed(String html, Pattern pattern) throws IOException {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		Matcher m = pattern.matcher(html);
		Date d = null;
		while (m.find()) {
			if (m.group(DATE) != null) {
				d = parseDate(m.group(DATE));
			} else {
				String title = m.group(TITLE);
				String filename = title.replace(" ", ".") + ".mp4";
				FeedItem feedItem = FeedItemBuilder.start().withTitle(title).withDescription(title).withDate(d) //
						.withLocation(m.group(LOCATION)).withFilename(filename).get();
				feedItems.add(feedItem);
			}
		}
		return feedItems;
	}

	private Pattern getEntryPattern(String label) {
		return MAGNET ? getMagnetPattern(label) : getTorrentPattern(label);
	}

	private Pattern getTorrentPattern(String label) {
		return Pattern.compile(PATTERN_1 + label + PATTERN_2_TORRENT, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	private Pattern getMagnetPattern(String label) {
		return Pattern.compile(PATTERN_1 + label + PATTERN_2_MAGNET, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	private Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd, MMMMM, yyyy", Locale.US).parse(date);
		} catch (java.text.ParseException e) {
			return new Date();
		}
	}

	private static Integer getTvShowId(String showName) throws IOException {
		String key = showName.toLowerCase();
		if (!TV_SHOW_IDS.containsKey(key)) {
			buildCache();
		}
		return TV_SHOW_IDS.get(key);
	}

	private String computeTitle(final String showName, int season) {
		return ShowUtils.upperCaseString(showName) + " " + getFormattedShowSeason(season);
	}

	private static void buildCache() throws IOException {
		String html = Jsoup.connect("http://eztv.ch").userAgent("Mozilla").get().html();
		Matcher m = Pattern.compile("<option value=\"(\\d+)\">(((?!</option>).)*)</option>").matcher(html);
		while (m.find()) {
			String showName = m.group(2).toLowerCase();
			Integer id = Integer.parseInt(m.group(1));
			TV_SHOW_IDS.put(showName, id);
		}
	}

	@PostConstruct
	public void postConstruct() {
		try {
			buildCache();
		} catch (Exception e) {
			// cache will be build later anyway
		}
	}

	@PreDestroy
	public void cleanUp() {
		TV_SHOW_IDS.clear();
	}

}
