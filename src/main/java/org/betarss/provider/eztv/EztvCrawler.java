package org.betarss.provider.eztv;

import static org.betarss.utils.BetarssUtils.doTry;
import static org.betarss.utils.BetarssUtils.parseDefaultDate;
import static org.jsoup.Jsoup.connect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.BetarssSearch;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedItem;
import org.betarss.domain.builder.FeedBuilder;
import org.betarss.domain.builder.FeedItemBuilder;
import org.betarss.provider.ICrawler;
import org.betarss.utils.BetarssUtils.Function;
import org.betarss.utils.ShowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	private EztvCache eztvCache;

	@Override
	public Feed getFeed(final BetarssSearch betarssSearch) throws IOException {
		return betarssSearch.showEpisode.show != null ? getShowSeasonFeed(betarssSearch) : getLastItemsFeed(betarssSearch);
	}

	private Feed getShowSeasonFeed(BetarssSearch betarssSearch) throws IOException {
		String html = fetchHtml(betarssSearch);
		Pattern entryPattern = getEntryPattern(getSearchLabel(betarssSearch), betarssSearch.magnet);
		List<FeedItem> feedItems = getFeed(html, entryPattern);
		String feedTitle = computeTitle(betarssSearch);
		return FeedBuilder.start(). //
				withTitle(feedTitle). //
				withFeedItems(feedItems).get();
	}

	public Feed getLastItemsFeed(BetarssSearch betarssSearch) throws IOException {
		String html = fetchHtml(betarssSearch);
		List<FeedItem> feedItems = getFeed(html, getEntryPattern(null, betarssSearch.magnet));
		return FeedBuilder.start(). //
				withTitle("EZTV feed"). //
				withFeedItems(feedItems).get();
	}

	private String fetchHtml(final BetarssSearch betarssSearch) {
		return doTry(FETCH_HTML_RETRY_NUMBER, new Function<String>() {

			@Override
			public String doCall() throws Exception {
				if (betarssSearch.showEpisode.show != null) {
					return connect(SEARCH_URL).userAgent("Mozilla/5.0").data("SearchString", getTvShowId(betarssSearch.showEpisode.show).toString())
							.post().html();
				}
				return connect(SEARCH_URL).userAgent("Mozilla/5.0").post().html();

			}

		});
	}

	private List<FeedItem> getFeed(String html, Pattern pattern) throws IOException {
		List<FeedItem> results = new ArrayList<FeedItem>();
		Matcher matcher = pattern.matcher(html);
		Date date = null;
		while (matcher.find()) {
			if (matcher.group(DATE) != null) {
				date = parseDefaultDate(matcher.group(DATE), "dd, MMMMM, yyyy", Locale.US);
			} else {
				results.add(getFeedItem(matcher, date));
			}
		}
		return results;
	}

	private FeedItem getFeedItem(Matcher matcher, Date date) {
		String title = matcher.group(TITLE);
		String filename = title.replace(" ", ".") + ".mp4";
		FeedItem feedItem = FeedItemBuilder.start(). //
				withTitle(title). //
				withDescription(title). //
				withDate(date). //
				withLocation(matcher.group(LOCATION)). //
				withFilename(filename).get();
		return feedItem;
	}

	private Integer getTvShowId(String showName) {
		return eztvCache.get(showName.toLowerCase());
	}

	private String computeTitle(BetarssSearch betarssSearch) {
		return ShowUtils.upperCaseString(betarssSearch.showEpisode.show) + " " + ShowUtils.getFormattedShowSeason(betarssSearch.showEpisode.season);
	}

	private static Pattern getEntryPattern(String label, boolean magnet) {
		return MAGNET ? getMagnetPattern(label) : getTorrentPattern(label);
	}

	private static Pattern getTorrentPattern(String label) {
		return Pattern.compile(PATTERN_1 + label + PATTERN_2_TORRENT, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	private static Pattern getMagnetPattern(String label) {
		return Pattern.compile(PATTERN_1 + label + PATTERN_2_MAGNET, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	private String getSearchLabel(BetarssSearch betarssSearch) {
		return betarssSearch.showEpisode.show + " " + ShowUtils.getFormattedShowSeason(betarssSearch.showEpisode.season);
	}

}