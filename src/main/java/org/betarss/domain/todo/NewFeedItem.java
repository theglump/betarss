package org.betarss.domain.todo;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.betarss.domain.Torrent;

import com.google.common.base.Objects;

//TODO P1 : Les FeedItem sont utilisés uniquement lors de la génération de RSS (les crawlers récupérent magnet et torrent et on filter ici)
@XmlRootElement(name = "item")
@XmlType(propOrder = { "title", "description", "location", "filename", "date" })
public class NewFeedItem {

	public Torrent<?> torrent;
	public boolean useMagnet;

	public NewFeedItem(Torrent<?> torrent, boolean useMagnet) {
		this.torrent = torrent;
		this.useMagnet = useMagnet;
	}

	@XmlElement(name = "title")
	public String getTitle() {
		return torrent.title;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return torrent.description;
	}

	@XmlElement(name = "link")
	public String getLocation() {
		return useMagnet ? torrent.magnet : torrent.url;
	}

	@XmlElement(name = "filename")
	public String getFilename() {
		return torrent.filename;
	}

	@XmlElement(name = "pubDate")
	public Date getDate() {
		return torrent.date;
	}

	@Override
	@SuppressWarnings(value = "deprecation")
	public String toString() {
		return Objects.toStringHelper(this).add("title", getTitle()).add("description", getDescription()). //
				add("location", getLocation()).add("filename", getFilename()).add("date", getDate()).toString();
	}

}
