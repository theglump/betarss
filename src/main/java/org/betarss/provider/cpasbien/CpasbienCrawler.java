package org.betarss.provider.cpasbien;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Torrent;
import org.betarss.infrastructure.http.HttpClient;
import org.betarss.infrastructure.http.NetHttpClient.Parameter;
import org.betarss.producer.BacklinkHelper;
import org.betarss.provider.Crawler;
import org.betarss.utils.Shows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class CpasbienCrawler implements Crawler {

	private static final String SEARCH_URL = "http://www.cpasbien.pw/recherche/";
	private static final String LAST_ITEMS_URL = "http://www.cpasbien.pw/view_cat.php?categorie=series";
	private static final Pattern EPISODE_ITEM_PATTERN = Pattern.compile("(.*dl-torrent.*/(.*)\\.html.*(\\d+/\\d+/\\d+).*>(.*)<.*)",
			Pattern.CASE_INSENSITIVE);

	private static final int TORRENT_NAME = 2;
	private static final int DATE = 3;
	private static final int TITLE = 4;

	@Autowired
	private BacklinkHelper backlinkHelper;
	
	@Autowired
	@Qualifier("httpClient")
	private HttpClient httpClient;

	@Override
	public List<Torrent> doCrawl(String show, Integer season, boolean backlink) {
		if (show == null) {
			return getFeed(backlink);
		}
		String html = fetchHtml(show, season);
		List<Torrent> torrents = getFeedItems(html, backlink);
		return torrents;
	}

	public List<Torrent> getFeed(boolean backlink) {
		return getFeedItems(fetchHtml(), backlink);
	}

	private List<Torrent> getFeedItems(String html, boolean backlink) {
		List<Torrent> torrents = Lists.newArrayList();
		Matcher m = EPISODE_ITEM_PATTERN.matcher(html);
		while (m.find()) {
			torrents.add(createFeed(m, backlink));
		}
		return torrents;
	}

	private Torrent createFeed(Matcher m, boolean backlink) {
		Torrent torrent = new Torrent();
		torrent.title = m.group(TITLE);
		torrent.description = m.group(TITLE);
		torrent.url = getLocation(m.group(TORRENT_NAME), backlink);
		torrent.date = parseDate(m.group(DATE));
		return torrent;
	}

	private String fetchHtml() {
		return httpClient.get(LAST_ITEMS_URL);
	}

	private String fetchHtml(String showName, Integer season) {
		String searchString = getSearchString(showName, season);
		return httpClient.post(SEARCH_URL, Parameter.create("champ_recherche", searchString));
	}

	private Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(date);
		} catch (java.text.ParseException e) {
			return new Date();
		}
	}

	private String getLocation(String torrentName, boolean backlink) {
		String location = "http://www.cpasbien.pw/telechargement/" + torrentName + ".torrent";
		return backlink ? backlinkHelper.create(location) : location;
	}

	private String getSearchString(String showName, Integer season) {
		return showName + " " + Shows.formatSeason(season);
	}

}
