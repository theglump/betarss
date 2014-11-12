package org.betarss.domain;

import java.util.Date;

public class FeedItem {

	private String title;
	private String description;
	private String location;
	private Date date;
	
	public FeedItem(FeedItemBuilder builder) {
		this.title = builder.title;
		this.description = builder.description;
		this.location = builder.location;
		this.date = builder.date;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLocation() {
		return location;
	}
	
	public Date getDate() {
		return date;
	}
	
}
