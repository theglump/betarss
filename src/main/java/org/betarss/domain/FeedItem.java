package org.betarss.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "item")
@XmlType(propOrder = { "title", "description", "location", "date" })
public class FeedItem {

	private String title;
	private String description;
	private String location;
	private Date date;

	private FeedItem() {
	}

	public FeedItem(FeedItemBuilder builder) {
		this.title = builder.title;
		this.description = builder.description;
		this.location = builder.location;
		this.date = builder.date;
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	@XmlElement(name = "link")
	public String getLocation() {
		return location;
	}

	@XmlElement(name = "pubDate")
	public Date getDate() {
		return date;
	}

}
