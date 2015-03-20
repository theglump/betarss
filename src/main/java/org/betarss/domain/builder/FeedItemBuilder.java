package org.betarss.domain.builder;

import java.util.Date;

import org.betarss.domain.FeedItem;

public class FeedItemBuilder {

	public String title;
	public String description;
	public String location;
	public String filename;
	public Date date;

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

	public FeedItemBuilder withFilename(String filename) {
		this.filename = filename;
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
