package org.betarss.producer;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.betarss.domain.Feed;
import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.utils.HttpUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public class RssProducer implements Producer {

	@Override
	public HttpEntity<byte[]> produceAsHttpEntity(String title, List<Torrent<ShowEpisode>> torrents, boolean magnet) throws Exception {
		String data = produce(title, torrents, magnet);
		return HttpUtils.httpEntity("application", "xml", data);
	}

	@Override
	public String produce(String title, List<Torrent<ShowEpisode>> torrents, boolean magnet) throws Exception {
		Feed feed = new Feed(title, torrents, magnet);
		JAXBContext context = JAXBContext.newInstance(Rss2.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(new Rss2(feed), sw);
		return sw.toString();
	}

	@XmlRootElement(name = "rss")
	private static final class Rss2 {

		Feed feed;

		private Rss2() {
		}

		private Rss2(Feed feed) {
			this.feed = feed;
		}

		@XmlAttribute(name = "version")
		String getVersion() {
			return "2.0";
		}

		@XmlElement(name = "channel")
		Feed getFeed() {
			return feed;
		}

	}

}
