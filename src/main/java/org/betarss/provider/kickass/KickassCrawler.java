package org.betarss.provider.kickass;

import static org.betarss.utils.BetarssUtils.multiThreadCalls;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Torrent;
import org.betarss.infrastructure.http.HttpClient;
import org.betarss.infrastructure.http.NetHttpClient.Parameter;
import org.betarss.provider.Crawler;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Procedure;
import org.betarss.utils.Shows;
import org.betarss.utils.Torrents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class KickassCrawler implements Crawler {

	private static final String LAST_ITEMS_URLS = "https://kickass.to/tv/";
	private static final String SEARCH_URL = "https://kickass.to/usearch/";

	private static final Pattern ITEMS_PATTERN = Pattern.compile("((<tr class=\"(odd|even)\"((?!red lasttd center).)*))", Pattern.DOTALL
			| Pattern.CASE_INSENSITIVE);

	private static final Pattern ITEM_PATTERN = Pattern.compile(
			"<a title=\"Torrent magnet link\" href=\"(((?!\").)*)\"(.*)<a title=\"Download torrent file\" href=\"(((?!\").)*)\"(.*)" + //
					"<a href=\"(((?!\").)*)\" class=\"cellMainLink\">(((?!</a>).)*)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	private static final Pattern DATE_PATTERN = Pattern.compile("Added on (\\w+ \\d+, \\d+) by", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	private static final int MAGNET = 1;
	private static final int TORRENT_LINK = 4;
	private static final int PAGE_LINK = 7;
	private static final int RAW_TITLE = 9;

	@Autowired
	@Qualifier("httpClient")
	private HttpClient httpClient;

	@Override
	public List<Torrent> doCrawl(String show, Integer season, boolean backlink) {
		if (show == null) {
			return crawlNewTorrents(show, season);
		}
		return crawlForSeason(show, season);
	}

	public List<Torrent> crawlForSeason(String show, Integer season) {
		return Torrents.sort(getTorrents(fetchHtml(show, season)));
	}

	public List<Torrent> crawlNewTorrents(String show, Integer season) {
		return Torrents.sort(getTorrents(fetchHtml()));
	}

	private List<Torrent> getTorrents(String html) {
		final List<Torrent> feedItems = new CopyOnWriteArrayList<Torrent>();
		List<Procedure> procedures = Lists.newArrayList();
		Matcher m = ITEMS_PATTERN.matcher(html);
		while (m.find()) {
			final String itemHtml = m.group(0);
			procedures.add(new Procedure() {

				@Override
				public void doCall() throws Exception {
					Torrent torrent = createTorrent(itemHtml);
					feedItems.add(torrent);
				}

			});
		}
		multiThreadCalls(procedures, 120);
		return feedItems;
	}

	private Torrent createTorrent(String itemHtml) {
		Matcher matcher = ITEM_PATTERN.matcher(itemHtml);
		if (matcher.find()) {
			Torrent torrent = new Torrent();
			torrent.title = stripHtml(matcher.group(RAW_TITLE));
			torrent.magnet = matcher.group(MAGNET);
			torrent.url = matcher.group(TORRENT_LINK);
			torrent.date = getDate(matcher.group(PAGE_LINK));
			return torrent;
		}
		return null;
	}

	private Date getDate(String torrentPageUrl) {
		String html = httpClient.get("https://kickass.to" + torrentPageUrl);
		Matcher dateMatcher = DATE_PATTERN.matcher(html);
		if (dateMatcher.find()) {
			return BetarssUtils.parseDate(dateMatcher.group(1), "MMM dd, yyyy", Locale.US);
		}
		return null;
	}

	private String fetchHtml() {
		try {
			return httpClient.get(LAST_ITEMS_URLS);
		} catch (Exception e) {
		}
		return "";
	}

	private String fetchHtml(String showName, Integer season) {
		try {
			String searchString = getSearchString(showName, season);
			return httpClient.post(SEARCH_URL, Parameter.create("q", searchString));
		} catch (Exception e) {
		}
		return "";
	}

	private String getSearchString(String showName, Integer season) {
		return showName + " " + Shows.formatSeason(season) + " EZTV";
	}

	private String stripHtml(String title) {
		return title.replaceAll("<strong class=\"red\">", "").replace("</strong>", "");
	}

}
