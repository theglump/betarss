package org.betarss.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.betarss.domain.builder.FeedBuilder;

import com.google.common.collect.ImmutableList;

@XmlRootElement(name = "channel")
public class Feed {

	private String title;
	private List<FeedItem> feedItems;

	@SuppressWarnings("unused")
	private Feed() {

	}

	public Feed(FeedBuilder builder) {
		this.title = builder.title;
		this.feedItems = builder.feedItems;
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
