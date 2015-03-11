package org.betarss.core.internal;

import static org.betarss.utils.ShowUtils.getFormattedShowSeason;
import static org.betarss.utils.ShowUtils.upperCaseString;

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
import org.betarss.exception.BetarssException;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class EztvCrawler implements ICrawler {

	private static final int FETCH_HTML_RETRY_NUMBER = 10;

	private static final int DATE = 2;
	private static final int TITLE = 4;
	private static final int LOCATION = 7;

	private static final Map<String, Integer> TV_SHOW_IDS = Maps.newConcurrentMap();

	@Override
	public Feed getFeed(String showName, int season) throws IOException {
		String html = tryToFetchHtml(showName);
		List<FeedItem> feedItems = getFeed(html, showName, season);
		return FeedBuilder.start().withTitle(upperCaseString(showName) + " " + getFormattedShowSeason(season)).withFeedItems(feedItems).get();
	}

	private String tryToFetchHtml(String showName) {
		int times = 0;
		String html = null;
		while (html == null) {
			try {
				html = fetchHtml(showName);
			} catch (Exception e) {
				if (++times == FETCH_HTML_RETRY_NUMBER) {
					throw new BetarssException(e);
				}
			}
		}
		return html;
	}

	private String fetchHtml(String showName) throws IOException {
		String html;
		html = Jsoup.connect("https://eztv.ch/search/") //
				.userAgent("Mozilla/5.0") //
				.data("SearchString", getTvShowId(showName).toString()) //
				.post() //
				.html();
		return html;
	}

	private List<FeedItem> getFeed(String html, String showName, int season) throws IOException {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		Matcher m = getEpidodePattern(showName, season).matcher(html);
		Date d = null;
		while (m.find()) {
			if (m.group(DATE) != null) {
				d = parseDate(m.group(DATE));
			} else {
				String title = m.group(TITLE);
				FeedItem feedItem = FeedItemBuilder.start().withTitle(title).withDescription(title).withDate(d).withLocation(m.group(LOCATION)).get();
				feedItems.add(feedItem);
			}
		}
		return feedItems;
	}

	private Pattern getEpidodePattern(String showName, int season) {
		return Pattern.compile("(Added on: <b>(\\d+, \\w+, \\d+)</b>)|(title=\"(" + showName + " " + getFormattedShowSeason(season)
				+ "((?!\").)*MB\\))\"((?!forum_thread_post_end).)*<a href=\"(magnet((?!\").)*)\"((?!magnet).)*)", Pattern.DOTALL
				| Pattern.CASE_INSENSITIVE);
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
			// cache will be build anyways
		}
	}

	@PreDestroy
	public void cleanUp() {
		TV_SHOW_IDS.clear();
	}

}
