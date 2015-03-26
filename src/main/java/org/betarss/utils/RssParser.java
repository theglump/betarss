package org.betarss.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.betarss.domain.Torrent;
import org.betarss.exception.BetarssException;

import com.google.common.collect.Lists;

public class RssParser {
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String AUTHOR = "author";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";

	final URL url;

	public RssParser(String feedUrl) {
		try {
			this.url = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Torrent> parse() {
		return parse("title");
	}

	public List<Torrent> parseShowRss() {
		return parse("rawtitle");
	}

	private List<Torrent> parse(String titleElementName) {
		List<Torrent> torrents = Lists.newArrayList();
		try {
			boolean isFeedHeader = true;
			String description = "";
			String title = "";
			String link = "";
			String pubdate = "";

			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			InputStream in = read();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					String localPart = event.asStartElement().getName().getLocalPart();
					if (ITEM.equals(localPart) && isFeedHeader) {
						isFeedHeader = false;
					} else if (titleElementName.equals(localPart)) {
						title = getCharacterData(event, eventReader);
					} else if (DESCRIPTION.equals(localPart)) {
						description = getCharacterData(event, eventReader);
					} else if (LINK.equals(localPart)) {
						link = getCharacterData(event, eventReader);
					} else if (PUB_DATE.equals(localPart)) {
						pubdate = getCharacterData(event, eventReader);
					}

				} else if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
						Torrent torrent = new Torrent();
						torrent.title = title;
						torrent.description = description;
						if (link.startsWith("magnet")) {
							torrent.magnet = link;
						} else {
							torrent.url = link;
						}
						torrent.date = new Date(pubdate);
						torrents.add(torrent);
						continue;
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new BetarssException("Error during rss parsing of " + url, e);
		}
		return torrents;
	}

	private String getCharacterData(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		return result;
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
