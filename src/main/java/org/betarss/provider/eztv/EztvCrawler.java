package org.betarss.provider.eztv;

import static org.betarss.utils.BetarssUtils.doTry;
import static org.betarss.utils.BetarssUtils.parseDefaultDate;
import static org.jsoup.Jsoup.connect;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.betarss.provider.ICrawler;
import org.betarss.utils.BetarssUtils.Function;
import org.betarss.utils.ShowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class EztvCrawler implements ICrawler {

	private static final String SEARCH_URL = "https://eztv.ch/search/";

	private static final String PATTERN_1 = "(Added on: <b>(\\d+, \\w+, \\d+)</b>)|(title=\"(";

	//><a href="" data-file="The.Good.Wife.S06E16.HDTV.x264-LOL.torrent" data-url="http%3A%2F%2Fre.zoink.it%2Fg%2F14D33BC0B3021FE081DC31AB352E0E4612E85EFA" 

	private static final String PATTERN_2 = "((?!\").)*MB\\))\"((?!forum_thread_post_end).)*<a href=\"(magnet((?!\").)*)\"((?!magnet).)*)<a href=\"(((?!\").)*) class=\"download_1\"(((?!download_1).)*)";

	private static final int FETCH_HTML_RETRY_NUMBER = 10;

	private static final int DATE = 2;
	private static final int TITLE = 4;
	private static final int MAGNET = 7;
	private static final int URL = 8;

	@Autowired
	private EztvCache eztvCache;

	@Override
	public List<Torrent<ShowEpisode>> doCrawl(String show, Integer season) throws IOException, FeedFilterException {
		String searchString = searchString(show, season);
		return getTorrents(html(show), entryPattern(searchString));
	}

	private String html(final String show) {
		return doTry(FETCH_HTML_RETRY_NUMBER, new Function<String>() {

			@Override
			public String doCall() throws Exception {
				if (show != null) {
					return connect(SEARCH_URL).userAgent("Mozilla/5.0").data("SearchString", getTvShowId(show).toString()).post().html();
				}
				return connect(SEARCH_URL).userAgent("Mozilla/5.0").post().html();

			}

		});
	}

	private List<Torrent<ShowEpisode>> getTorrents(String html, Pattern pattern) throws IOException {
		List<Torrent<ShowEpisode>> results = Lists.newArrayList();
		Matcher matcher = pattern.matcher(html);
		Date date = null;
		while (matcher.find()) {
			if (matcher.group(DATE) != null) {
				date = parseDate(matcher);
			} else {
				Torrent<ShowEpisode> torrent = getTorrent(matcher, date);
				results.add(torrent);
			}
		}
		return results;
	}

	private Torrent<ShowEpisode> getTorrent(Matcher matcher, Date date) {
		Torrent<ShowEpisode> torrent = new Torrent<ShowEpisode>();
		torrent.title = matcher.group(TITLE);
		torrent.description = torrent.title;
		torrent.filename = filename(torrent);
		torrent.date = date;
		torrent.magnet = matcher.group(MAGNET);
		torrent.url = matcher.group(URL);
		return torrent;
	}

	private Date parseDate(Matcher matcher) {
		return parseDefaultDate(matcher.group(DATE), "dd, MMMMM, yyyy", Locale.US);
	}

	private String filename(Torrent<ShowEpisode> torrent) {
		return torrent.title.replace(" ", ".") + ".mp4";
	}

	private Integer getTvShowId(String showName) {
		return eztvCache.get(showName.toLowerCase());
	}

	private static Pattern entryPattern(String label) {
		return Pattern.compile(PATTERN_1 + label + PATTERN_2, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}

	private String searchString(String show, Integer season) {
		StringBuilder sb = new StringBuilder();
		if (show != null) {
			sb.append(show);
			if (season != null) {
				sb.append(" ").append(ShowUtils.formatSeason(season));
			}
		}
		return sb.toString();
	}

}