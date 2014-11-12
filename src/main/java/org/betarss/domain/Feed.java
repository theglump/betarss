package org.betarss.domain;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class Feed {

	private String title;
	private List<FeedItem> feedItems; 
	
	public Feed(FeedBuilder builder) {
		this.title = builder.title;
		this.feedItems = builder.feedItems;
	}

	public String getTitle() {
		return title;
	}
	
	public List<FeedItem> getFeedItems() {
		return ImmutableList.copyOf(feedItems);
	}
	

}
