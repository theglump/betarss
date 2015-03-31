package org.betarss.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@XmlRootElement(name = "channel")
@XmlType(propOrder = { "title", "description", "feedItems" })
public class Feed {

	private String title;
	private String description;
	private List<FeedItem> feedItems;

	@SuppressWarnings("unused")
	private Feed() {

	}

	public Feed(String title, String description, List<Torrent> torrents, boolean magnet) {
		this.title = title;
		this.description = description;
		this.feedItems = Lists.newArrayList();
		for (Torrent torrent : torrents) {
			this.feedItems.add(new FeedItem(torrent, magnet));
		}
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	@XmlElement(name = "item")
	public List<FeedItem> getFeedItems() {
		return ImmutableList.copyOf(feedItems);
	}

}
