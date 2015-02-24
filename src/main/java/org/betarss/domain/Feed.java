package org.betarss.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

	//	  // XmLElementWrapper generates a wrapper element around XML representation
	//	  @XmlElementWrapper(name = "bookList")
	// XmlElement sets the name of the entities
	@XmlElement(name = "item")
	public List<FeedItem> getFeedItems() {
		return ImmutableList.copyOf(feedItems);
	}

}
