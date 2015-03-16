package org.betarss.provider;

import static org.betarss.utils.ShowUtils.upperCaseString;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.betarss.utils.ShowUtils;
import org.betarss.utils.Utils;
import org.betarss.utils.Utils.Function;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class KickassSearchEngine implements ISearchEngine {

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
	public Provider getProvider() {
		return Provider.KICKASS;
	}

	@Override
	public String getFilter(Language language, Quality quality) {
		StringBuilder filter = new StringBuilder();
		if (quality == Quality.SD) {
			filter.append("!720p");
		} else if (quality == Quality.HD) {
			filter.append("720p");
		}
		return filter.toString();
	}

	@Override
	public Feed getFeed(FeedSearch feedSearch) throws IOException {
		if (feedSearch.show == null) {
			return getLastEpisodes(feedSearch);
		}
		return getShowSeasonFeed(feedSearch);
	}

	public Feed getShowSeasonFeed(FeedSearch feedSearch) throws IOException {
		String html = fetchHtml(feedSearch.show, feedSearch.season);
		List<FeedItem> feedItems = getFeedItems(html, feedSearch.magnet, feedSearch.date);
		return FeedBuilder.start().withTitle(computeTitle(feedSearch)).withFeedItems(feedItems).get();
	}

	public Feed getLastEpisodes(FeedSearch feedSearch) throws IOException {
		List<FeedItem> feedItems = getFeedItems(fetchHtml(), feedSearch.magnet, feedSearch.date);
		return FeedBuilder.start().withFeedItems(feedItems).get();
	}

	private List<FeedItem> getFeedItems(String html, final boolean magnet, final boolean date) throws IOException {
		List<Function<FeedItem>> functions = Lists.newArrayList();
		Matcher m = ITEMS_PATTERN.matcher(html);
		while (m.find()) {
			final String itemHtml = m.group(0);
			functions.add(new Function<FeedItem>() {

				@Override
				public FeedItem doCall() throws Exception {
					return createFeed(itemHtml, magnet, date);
				}

			});
		}
		return Utils.multiThreadCalls(functions, 120);
	}

	private FeedItem createFeed(String itemHtml, boolean magnet, boolean date) throws IOException {
		Matcher matcher = ITEM_PATTERN.matcher(itemHtml);
		if (matcher.find()) {
			String title = stripHtml(matcher.group(RAW_TITLE));
			String location = magnet ? matcher.group(MAGNET) : matcher.group(TORRENT_LINK);
			Date torrentDate = date ? getDate(matcher.group(PAGE_LINK)) : null;
			return FeedItemBuilder.start(). //
					withTitle(title). //
					withLocation(location). //
					withDescription(title). //
					withDate(torrentDate).get();
		}
		return null;
	}

	private Date getDate(String torrentPageUrl) throws IOException {
		String html = Jsoup.connect("https://kickass.to" + torrentPageUrl).userAgent("Mozilla/5.0").get().html();
		Matcher dateMatcher = DATE_PATTERN.matcher(html);
		if (dateMatcher.find()) {
			return Utils.parseDefaultDate(dateMatcher.group(1), "MMM dd, yyyy", Locale.US);
		}
		return null;
	}

	private String fetchHtml() throws IOException {
		return Jsoup.connect("https://kickass.to/tv/").userAgent("Mozilla/5.0").get().html();
	}

	private String fetchHtml(String showName, int season) throws IOException {
		String searchString = getSearchString(showName, season);
		return Jsoup.connect("https://kickass.to/usearch/").userAgent("Mozilla/5.0").data("q", searchString).post().html();
	}

	private String getSearchString(String showName, int season) {
		return showName + " " + ShowUtils.getFormattedShowSeason(season) + " EZTV";
	}

	private String stripHtml(String title) {
		return title.replaceAll("<strong class=\"red\">", "").replace("</strong>", "");
	}

	private String computeTitle(FeedSearch feedSearch) {
		return upperCaseString(feedSearch.show) + " " + ShowUtils.getFormattedShowSeason(feedSearch.season);
	}

}
