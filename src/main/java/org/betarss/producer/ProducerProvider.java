package org.betarss.producer;

import java.util.Map;

import org.betarss.domain.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class ProducerProvider {

	private Map<Mode, Producer> producers;

	@Autowired
	private RssProducer rssProducer;

	@Autowired
	private UrlProducer urlProducer;

	public Producer provide(Mode mode) {
		if (producers == null) {
			buildMap();
		}
		return producers.get(mode);
	}

	private void buildMap() {
		producers = Maps.newHashMap();
		producers.put(Mode.RSS, rssProducer);
		producers.put(Mode.URL, urlProducer);
	}
}
