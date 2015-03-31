package org.betarss.provider.showrss;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Torrent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;

public class ShowRssFeedParser {

	final String feed;

	public ShowRssFeedParser(String feed) {
		this.feed = feed;
	}

	public static List<Torrent> parse(String feed) {
		return new ShowRssFeedParser(feed).parse();
	}

	@SuppressWarnings("deprecation")
	private List<Torrent> parse() {
		Document document = Jsoup.parse(feed);
		List<Torrent> torrents = Lists.newArrayList();
		for (Element element : document.getElementsByTag("item")) {
			Torrent torrent = new Torrent();
			torrent.title = element.getElementsByTag("title").text();
			torrent.description = element.getElementsByTag("description").text();
			Matcher matcher = Pattern.compile("href=\"(.*)\"").matcher(torrent.description);
			matcher.find();
			String link = matcher.group(1);
			if (link.startsWith("magnet")) {
				torrent.magnet = link;
			} else {
				torrent.url = link;
			}
			torrent.date = new Date(element.getElementsByTag("pubDate").text());
			torrents.add(torrent);
		}
		return torrents;
	}
}
