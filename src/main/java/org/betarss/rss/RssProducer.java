package org.betarss.rss;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedItem;
import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.domain.builder.FeedBuilder;
import org.betarss.domain.builder.FeedItemBuilder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class RssProducer {

	public String produceRSS2(String title, List<Torrent<ShowEpisode>> torrents, boolean magnet) throws Exception {
		Feed feed = mapToFeed(title, torrents, magnet);
		JAXBContext context = JAXBContext.newInstance(Rss2.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(new Rss2(feed), sw);
		return sw.toString();
	}

	private Feed mapToFeed(String title, List<Torrent<ShowEpisode>> torrents, boolean magnet) {
		List<FeedItem> feedItems = Lists.newArrayList();
		for (Torrent<ShowEpisode> torrent : torrents) {
			String location = magnet && isNotEmtpy(torrent.magnet) ? torrent.magnet : torrent.url;
			FeedItem feedItem = FeedItemBuilder.start().withTitle(torrent.title).withDescription(torrent.description).withFilename(torrent.filename)
					.withLocation(location).withDate(torrent.date).get();
			feedItems.add(feedItem);
		}
		return FeedBuilder.start().withTitle(title).withFeedItems(feedItems).get();
	}

	private boolean isNotEmtpy(String magnet) {
		return magnet != null && !magnet.isEmpty();
	}

	@XmlRootElement(name = "rss")
	private static final class Rss2 {

		Feed feed;

		private Rss2() {
		}

		private Rss2(Feed feed) {
			this.feed = feed;
		}

		@XmlAttribute(name = "version")
		String getVersion() {
			return "2.0";
		}

		@XmlElement(name = "channel")
		Feed getFeed() {
			return feed;
		}

	}

}
