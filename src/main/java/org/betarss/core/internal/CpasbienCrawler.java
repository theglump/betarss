package org.betarss.core.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.core.Crawler;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.domain.FeedItemBuilder;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.search.QueryParseException;

@Service
public class CpasbienCrawler implements Crawler {

	@Override
	public Feed getFeed(String showName, int season) throws IOException  {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		int episode = 1;
		while (true) {
			String html = Jsoup.connect("http://www.cpasbien.pe/recherche/").userAgent("Mozilla").data("champ_recherche", showName + " " + ShowUtils.getFormattedShowEpisode(season, episode)).post().html();
			Pattern regexp = Pattern.compile("(<a class=\"lien-rechercher\" href=\".*/(.*)\\.html.*(\\d+/\\d+/\\d+).*/> (.*)</a>)", 
					Pattern.CASE_INSENSITIVE);
	
			Matcher m = regexp.matcher(html);
			int nbFound = 0;
	
			while (m.find()) {
				String title = m.group(4);
				String location = "http://www.cpasbien.me/_torrents/" + m.group(2) + ".torrent"; 
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
