package org.betarss.provider;

import static org.betarss.utils.ShowUtils.upperCaseString;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.domain.FeedItemBuilder;
import org.betarss.domain.FeedSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.utils.FilterUtils;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class CpasbienSearchEngine implements ISearchEngine {

	private static final String SEARCH_URL = "http://www.cpasbien.pw/recherche/";
	private static final String LAST_ITEMS_URL = "http://www.cpasbien.pw/view_cat.php?categorie=series";
	private static final Pattern EPISODE_ITEM_PATTERN = Pattern.compile("(.*dl-torrent.*/(.*)\\.html.*(\\d+/\\d+/\\d+).*>(.*)<.*)",
			Pattern.CASE_INSENSITIVE);

	private static final int TORRENT_NAME = 2;
	private static final int DATE = 3;
	private static final int TITLE = 4;

	@Override
	public Provider getProvider() {
		return Provider.CPASBIEN;
	}

	@Override
	public String getFilter(Language language, Quality quality) {
		StringBuilder filter = new StringBuilder();
		if (language == Language.FR) {
			FilterUtils.appendFilterAnd(filter, "FRENCH");
		} else if (language == Language.VOSTFR) {
			FilterUtils.appendFilterAnd(filter, "VOSTFR");
		}
		if (quality == Quality.SD) {
			FilterUtils.appendFilterAnd(filter, "!720p");
		} else if (quality == Quality.HD) {
			FilterUtils.appendFilterAnd(filter, "720p");
		}
		return filter.toString();
	}

	@Override
	public Feed getFeed(FeedSearch feedSearch) throws IOException {
		if (feedSearch.show == null) {
			return getFeed();
		}
		String html = fetchHtml(feedSearch.show, feedSearch.season);
		List<FeedItem> feedItems = getFeedItems(html);
		return FeedBuilder.start().withTitle(computeTitle(feedSearch)).withFeedItems(feedItems).get();
	}

	public Feed getFeed() throws IOException {
		List<FeedItem> feedItems = getFeedItems(fetchHtml());
		return FeedBuilder.start().withFeedItems(feedItems).get();
	}

	private List<FeedItem> getFeedItems(String html) throws IOException {
		List<FeedItem> feedItems = Lists.newArrayList();
		Matcher m = EPISODE_ITEM_PATTERN.matcher(html);
		while (m.find()) {
			feedItems.add(createFeed(m));
		}
		return feedItems;
	}

	private FeedItem createFeed(Matcher m) {
		return FeedItemBuilder.start().withTitle(m.group(TITLE)). //
				withDescription(m.group(TITLE)). //
				withLocation(getLocation(m.group(TORRENT_NAME))). //
				withDate(parseDate(m.group(DATE))).get();
	}

	private String fetchHtml() throws IOException {
		return Jsoup.connect(LAST_ITEMS_URL).userAgent("Mozilla").get().html();
	}

	private String fetchHtml(String showName, int season) throws IOException {
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

	private String getSearchString(String showName, int season) {
		return showName + " " + ShowUtils.getFormattedShowSeason(season);
	}

	private String computeTitle(FeedSearch feedSearch) {
		return upperCaseString(feedSearch.show) + " " + ShowUtils.getFormattedShowSeason(feedSearch.season);
	}

}
