package org.betarss.domain;

import java.util.Date;

public class FeedItemBuilder {

	protected String title;
	protected String description;
	protected String location;
	protected Date date;

	public static FeedItemBuilder start() {
		return new FeedItemBuilder();
	}

	public FeedItemBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public FeedItemBuilder withDescription(String description) {
		this.description = description;
		return this;
	}

	public FeedItemBuilder withLocation(String location) {
		this.location = location;
		return this;
	}

	public FeedItemBuilder withDate(Date date) {
		this.date = date;
		return this;
	}

	public FeedItem get() {
		return new FeedItem(this);
	}

}
