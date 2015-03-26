package org.betarss.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@XmlRootElement(name = "channel")
public class Feed {

	private String title;
	private List<FeedItem> feedItems;

	@SuppressWarnings("unused")
	private Feed() {

	}

	public Feed(String title, List<Torrent> torrents, boolean magnet) {
		this.title = title;
		this.feedItems = Lists.newArrayList();
		for (Torrent torrent : torrents) {
			this.feedItems.add(new FeedItem(torrent, magnet));
		}
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	@XmlElement(name = "item")
	public List<FeedItem> getFeedItems() {
		return ImmutableList.copyOf(feedItems);
	}

}
