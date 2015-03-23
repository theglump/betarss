package org.betarss.provider.cpasbien;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.betarss.provider.ICrawler;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class CpasbienCrawler implements ICrawler {

	private static final String SEARCH_URL = "http://www.cpasbien.pw/recherche/";
	private static final String LAST_ITEMS_URL = "http://www.cpasbien.pw/view_cat.php?categorie=series";
	private static final Pattern EPISODE_ITEM_PATTERN = Pattern.compile("(.*dl-torrent.*/(.*)\\.html.*(\\d+/\\d+/\\d+).*>(.*)<.*)",
			Pattern.CASE_INSENSITIVE);

	private static final int TORRENT_NAME = 2;
	private static final int DATE = 3;
	private static final int TITLE = 4;

	@Override
	public List<Torrent<ShowEpisode>> doCrawl(String show, Integer season) throws IOException, FeedFilterException {
		if (show == null) {
			return getFeed();
		}
		String html = fetchHtml(show, season);
		List<Torrent<ShowEpisode>> torrents = getFeedItems(html);
		return torrents;
	}

	public List<Torrent<ShowEpisode>> getFeed() throws IOException {
		return getFeedItems(fetchHtml());
	}

	private List<Torrent<ShowEpisode>> getFeedItems(String html) throws IOException {
		List<Torrent<ShowEpisode>> torrents = Lists.newArrayList();
		Matcher m = EPISODE_ITEM_PATTERN.matcher(html);
		while (m.find()) {
			torrents.add(createFeed(m));
		}
		return torrents;
	}

	private Torrent<ShowEpisode> createFeed(Matcher m) {
		Torrent<ShowEpisode> torrent = new Torrent<ShowEpisode>();
		torrent.title = m.group(TITLE);
		torrent.description = m.group(TITLE);
		torrent.url = getLocation(m.group(TORRENT_NAME));
		torrent.date = parseDate(m.group(DATE));
		return torrent;
	}

	private String fetchHtml() throws IOException {
		return Jsoup.connect(LAST_ITEMS_URL).userAgent("Mozilla").get().html();
	}

	private String fetchHtml(String showName, Integer season) throws IOException {
		String searchString = getSearchString(showName, season);
		return Jsoup.connect(SEARCH_URL).userAgent("Mozilla").data("champ_recherche", searchString).post().html();
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

	private String getSearchString(String showName, Integer season) {
		return showName + " " + ShowUtils.formatSeason(season);
	}

}
