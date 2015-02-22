package org.betarss.core.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.betarss.core.IRssProducer;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedItem;
import org.springframework.stereotype.Service;

import de.nava.informa.core.ChannelExporterIF;
import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.exporters.RSS_2_0_Exporter;
import de.nava.informa.impl.basic.ChannelBuilder;

@Service
public class InformaRssProducer implements IRssProducer {

	public String produceRSS2(Feed feed) throws IOException {
		ChannelBuilder channelBuilder = new ChannelBuilder();
		ChannelIF channelIf = channelBuilder.createChannel(feed.getTitle());
		for (FeedItem feedItem : feed.getFeedItems()) {
			ItemIF itemIf = channelBuilder.createItem(channelIf, feedItem.getTitle(), feedItem.getDescription(), new URL(feedItem.getLocation()));
			itemIf.setDate(feedItem.getDate());
			itemIf.setGuid(channelBuilder.createItemGuid(itemIf, feedItem.getLocation(), true));
		}
		return channelToString(channelIf);
	}

	private String channelToString(ChannelIF channel) throws IOException {
		Writer w = new StringWriter();
		ChannelExporterIF exporter = new RSS_2_0_Exporter(w, "utf-8");
		exporter.write(channel);
		return w.toString();
	}

}
