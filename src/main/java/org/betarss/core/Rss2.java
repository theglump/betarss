package org.betarss.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.betarss.domain.Feed;

@XmlRootElement(name = "rss")
public class Rss2 {

	private Feed feed;

	private Rss2() {
	}

	public Rss2(Feed feed) {
		this.feed = feed;
	}

	@XmlAttribute(name = "version")
	public String getVersion() {
		return "2.0";
	}

	@XmlElement(name = "channel")
	public Feed getFeed() {
		return feed;
	}

}
