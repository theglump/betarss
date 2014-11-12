package org.betarss.domain;

import java.util.ArrayList;
import java.util.List;

public class FeedBuilder {

	protected String title;
	protected List<FeedItem> feedItems = new ArrayList<FeedItem>();
	
	public static FeedBuilder start() {
		return new FeedBuilder();
	}
	
	public FeedBuilder withTitle(String title) {
		this.title = title;
		return this;
	}
		
	public FeedBuilder withFeedItems(List<FeedItem> feedItems) {
		this.feedItems = feedItems;
		return this;
	}
	
	public FeedBuilder withRssFeedItem(FeedItem feedItem) {
		this.feedItems.add(feedItem);
		return this;
	}
	
	public Feed get() {
		return new Feed(this);
	}
	
}
