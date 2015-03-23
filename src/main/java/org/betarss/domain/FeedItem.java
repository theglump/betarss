package org.betarss.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

@XmlRootElement(name = "item")
@XmlType(propOrder = { "title", "description", "location", "filename", "date" })
public class FeedItem {

	private String title;
	private String description;
	private String location;
	private String filename;
	private Date date;

	@SuppressWarnings("unused")
	private FeedItem() {
	}

	public FeedItem(Torrent<?> torrent, boolean magnet) {
		this.title = torrent.title;
		this.description = torrent.title;
		this.location = magnet && isNotEmpty(torrent.magnet) ? torrent.magnet : torrent.url;
		this.filename = torrent.filename;
		this.date = torrent.date;
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

	@XmlElement(name = "filename")
	public String getFilename() {
		return filename;
	}

	@XmlElement(name = "pubDate")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	@SuppressWarnings(value = "deprecation")
	public String toString() {
		return Objects.toStringHelper(this).add("title", title).add("description", description). //
				add("location", location).add("filename", filename).add("date", date).toString();
	}

	private boolean isNotEmpty(String magnet) {
		return magnet != null && !magnet.isEmpty();
	}
}
