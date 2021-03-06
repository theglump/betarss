package org.betarss.provider.eztv;

import static org.betarss.utils.Shows.formatSeason;
import static org.betarss.utils.Shows.formatSeasonOldSchool;
import static org.betarss.utils.Strings.isNotEmpty;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Torrent;
import org.betarss.infrastructure.http.HttpClient;
import org.betarss.infrastructure.http.NetHttpClient.Parameter;
import org.betarss.provider.Crawler;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class EztvCrawler implements Crawler {

	private static final String LAST_ITEMS_URL = "https://eztv.ch/";
	private static final String SEARCH_URL = "https://eztv.ch/search/";

	private static final String PATTERN_1 = "(Added on: <b>(\\d+, \\w+, \\d+)</b>)|(title=\"(";
	private static final String PATTERN_2 = "((?!\").)*MB\\))\"((?!forum_thread_post_end).)*<a href=\"(magnet((?!\").)*)\"(((?!forum_thread_post_end).)*))";
	private static final Pattern URL_PATTERN = Pattern.compile("href=\"(((?!\").)*)\" class=\"download", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	private static final int FETCH_HTML_RETRY_NUMBER = 10;

	private static final int DATE = 2;
	private static final int TITLE = 4;
	private static final int MAGNET = 8;
	private static final int URLS_HTML = 10;

	private static boolean AVOID_ZOINK_TORRENT = false;

	@Autowired
	@Qualifier("httpClient")
	private HttpClient httpClient;

	@Autowired
	private EztvCache eztvCache;

	@Override
	public List<Torrent> doCrawl(String show, Integer season, boolean backlink) {
		return getTorrents(html(show), getEntryPattern(show, season));
	}

	private String html(final String show) {
		if (Strings.isNotEmpty(show)) {
			String showId = getTvShowId(show).toString();
			return httpClient.post(SEARCH_URL, FETCH_HTML_RETRY_NUMBER, Parameter.create("SearchString", showId));
		}
		return httpClient.post(LAST_ITEMS_URL, FETCH_HTML_RETRY_NUMBER);
	}

	private List<Torrent> getTorrents(String html, Pattern pattern) {
		List<Torrent> results = Lists.newArrayList();
		Matcher matcher = pattern.matcher(html);
		Date date = null;
		while (matcher.find()) {
			if (matcher.group(DATE) != null) {
				date = parseDate(matcher);
			} else {
				Torrent torrent = getTorrent(matcher, date);
				results.add(torrent);
			}
		}
		return results;
	}

	private Torrent getTorrent(Matcher matcher, Date date) {
		Torrent torrent = new Torrent();
		String title = matcher.group(TITLE);
		torrent.title = title;
		torrent.description = title;
		torrent.filename = filename(title);
		torrent.date = date;
		torrent.magnet = matcher.group(MAGNET);
		torrent.url = url(matcher.group(URLS_HTML));
		return torrent;
	}

	private String url(String urlsHtml) {
		Matcher matcher = URL_PATTERN.matcher(urlsHtml);
		String defaultResult = null;
		while (matcher.find()) {
			String url = matcher.group(1);
			if (url.contains("extratorrent")) {
				return url;
			} else if (url.contains("piratebay")) {
				continue;
			} else if (AVOID_ZOINK_TORRENT && url.contains("zoink")) {
				continue;
			}
			defaultResult = url;
		}
		return defaultResult;
	}

	private Date parseDate(Matcher matcher) {
		return BetarssUtils.parseDate(matcher.group(DATE), "dd, MMMMM, yyyy", Locale.US);
	}

	private String filename(String title) {
		return title.replace(" ", ".").replaceAll("\\.\\(.*\\)", "") + ".mp4";
	}

	private Integer getTvShowId(String showName) {
		return eztvCache.get(showName.toLowerCase());
	}

	private Pattern getEntryPattern(String show, Integer season) {
		return Pattern.compile(PATTERN_1 + searchPattern(show, season) + PATTERN_2, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	private String searchPattern(String show, Integer season) {
		StringBuilder sb = new StringBuilder();
		if (isNotEmpty(show)) {
			sb.append(show);
			if (season != null && season > 0) {
				sb.append(" (").append(formatSeason(season)).append("|").append(formatSeasonOldSchool(season)).append(")");
			}
		} else {
			sb.append("()");
		}
		return sb.toString();
	}

}