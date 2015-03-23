package org.betarss.provider.kickass;

import static org.betarss.utils.BetarssUtils.multiThreadCalls;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.betarss.provider.ICrawler;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Procedure;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class KickassCrawler implements ICrawler {

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

	@Override
	public List<Torrent<ShowEpisode>> doCrawl(String show, Integer season) throws IOException, FeedFilterException {
		if (show == null) {
			return crawlNewTorrents(show, season);
		}
		return crawlForSeason(show, season);
	}

	public List<Torrent<ShowEpisode>> crawlForSeason(String show, Integer season) throws IOException {
		return getTorrents(fetchHtml(show, season));
	}

	public List<Torrent<ShowEpisode>> crawlNewTorrents(String show, Integer season) throws IOException {
		return getTorrents(fetchHtml());
	}

	private List<Torrent<ShowEpisode>> getTorrents(String html) throws IOException {
		final List<Torrent<ShowEpisode>> feedItems = new CopyOnWriteArrayList<Torrent<ShowEpisode>>();
		List<Procedure> procedures = Lists.newArrayList();
		Matcher m = ITEMS_PATTERN.matcher(html);
		while (m.find()) {
			final String itemHtml = m.group(0);
			procedures.add(new Procedure() {

				@Override
				public void doCall() throws Exception {
					Torrent<ShowEpisode> torrent = createTorrent(itemHtml);
					feedItems.add(torrent);
				}

			});
		}
		multiThreadCalls(procedures, 120);
		return feedItems;
	}

	private Torrent<ShowEpisode> createTorrent(String itemHtml) throws IOException {
		Matcher matcher = ITEM_PATTERN.matcher(itemHtml);
		if (matcher.find()) {
			Torrent<ShowEpisode> torrent = new Torrent<ShowEpisode>();
			torrent.title = stripHtml(matcher.group(RAW_TITLE));
			torrent.magnet = matcher.group(MAGNET);
			torrent.url = matcher.group(TORRENT_LINK);
			torrent.date = getDate(matcher.group(PAGE_LINK));
			return torrent;
		}
		return null;
	}

	private Date getDate(String torrentPageUrl) throws IOException {
		String html = Jsoup.connect("https://kickass.to" + torrentPageUrl).userAgent("Mozilla/5.0").get().html();
		Matcher dateMatcher = DATE_PATTERN.matcher(html);
		if (dateMatcher.find()) {
			return BetarssUtils.parseDefaultDate(dateMatcher.group(1), "MMM dd, yyyy", Locale.US);
		}
		return null;
	}

	private String fetchHtml() throws IOException {
		String html = "";
		try {
			html = Jsoup.connect("https://kickass.to/tv/").userAgent("Mozilla/5.0").get().html();
		} catch (Exception e) {
		}
		return html;
	}

	private String fetchHtml(String showName, Integer season) throws IOException {
		String html = "";
		try {
			String searchString = getSearchString(showName, season);
			html = Jsoup.connect("https://kickass.to/usearch/").userAgent("Mozilla/5.0").data("q", searchString).post().html();
		} catch (Exception e) {
		}
		return html;
	}

	private String getSearchString(String showName, Integer season) {
		return showName + " " + ShowUtils.formatSeason(season) + " EZTV";
	}

	private String stripHtml(String title) {
		return title.replaceAll("<strong class=\"red\">", "").replace("</strong>", "");
	}

}
