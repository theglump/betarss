package org.betarss.core.internal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.core.ICrawler;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.domain.FeedItemBuilder;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class CpasbienCrawler implements ICrawler {

	@Override
	public Feed getFeed(String showName, int season) throws IOException {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		int episode = 1;
		while (true) {
			String html = Jsoup.connect("http://www.cpasbien.pw/recherche/").userAgent("Mozilla")
					.data("champ_recherche", showName + " " + ShowUtils.getFormattedShowEpisode(season, episode)).post().html();

			Pattern regexp = Pattern.compile("(.*dl-torrent.*/(.*)\\.html.*(\\d+/\\d+/\\d+).*>(.*)<.*)", Pattern.CASE_INSENSITIVE);

			Matcher m = regexp.matcher(html);
			int nbFound = 0;

			while (m.find()) {
				String title = m.group(4);
				String location = "http://www.cpasbien.pw/telechargement/" + m.group(2) + ".torrent";
				Date date = null;
				try {
					date = new SimpleDateFormat("dd/MM/yyyy").parse(m.group(3));
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}

				FeedItem feedItem = FeedItemBuilder.start().withTitle(title).withDescription(title).withLocation(location).withDate(date).get();
				feedItems.add(feedItem);

				nbFound = ShowUtils.hasMoreThanOneEp(title) ? 2 : 1;
			}

			if (nbFound == 0) {
				break;
			}
			episode += nbFound;
		}
		return FeedBuilder.start().withFeedItems(feedItems).get();

	}
}
