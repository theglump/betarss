package org.betarss.domain.builder;

import java.util.ArrayList;
import java.util.List;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedItem;

public class FeedBuilder {

	public String title;
	public List<FeedItem> feedItems = new ArrayList<FeedItem>();

	public static FeedBuilder start() {
		return new FeedBuilder();
	}

	public FeedBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public FeedBuilder withFeedItems(List<FeedItem> feedItems) {
		this.feedItems.addAll(feedItems);
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
