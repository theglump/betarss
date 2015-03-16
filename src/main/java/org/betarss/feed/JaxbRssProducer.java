package org.betarss.feed;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.betarss.domain.Feed;
import org.springframework.stereotype.Service;

@Service
public class JaxbRssProducer implements IRssProducer {

	public String produceRSS2(Feed feed) throws Exception {
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
