package org.betarss.controller;

import org.betarss.core.BetaseriesFeedProcessor;
import org.betarss.core.CrawlerProvider;
import org.betarss.core.FeedFilter;
import org.betarss.core.ICrawler;
import org.betarss.core.RssProducer;
import org.betarss.domain.Feed;
import org.betarss.domain.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BetarssController {

	@Autowired
	private CrawlerProvider crawlerProvider;

	@Autowired
	private FeedFilter feedFilter;

	@Autowired
	private RssProducer rssProducer;

	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@RequestParam(required = true) String show, //
			@RequestParam(required = true) int season, //
			@RequestParam(required = false, defaultValue = "vostfr") String language, //
			@RequestParam(required = false) String filter) throws Exception {

		Feed feed = getCrawler(language).getFeed(show, season);

		filter = decorateFilter(filter, language);
		if (!isEmpty(filter)) {
			feed = feedFilter.filter(feed, filter);
		}

		return httpEntity(rssProducer.produceRSS2(feed));
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> betaseries( //
			@RequestParam(required = true) String login, //
			@RequestParam(required = false, defaultValue = "vostfr") String language, //
			@RequestParam(required = false) String filter) throws Exception {

		BetaseriesFeedProcessor processor = new BetaseriesFeedProcessor(getCrawler(language));
		Feed feed = processor.getFeed(login);

		filter = decorateFilter(filter, language);
		if (!isEmpty(filter)) {
			feed = feedFilter.filter(feed, filter);
		}

		return httpEntity(rssProducer.produceRSS2(feed));
	}

	private ICrawler getCrawler(String language) {
		return crawlerProvider.provide(Language.get(language));
	}

	private HttpEntity<byte[]> httpEntity(String xml) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "xml"));
		header.setContentLength(xml.length());
		return new HttpEntity<byte[]>(xml.getBytes(), header);
	}

	private String decorateFilter(String filter, String language) {
		return concatFilters(filter, Language.get(language).getFilter());
	}

	private String concatFilters(String filter1, String filter2) {
		if (isEmpty(filter1)) {
			return filter2;
		}
		return isEmpty(filter2) ? filter1 : filter1 + "^" + filter2;
	}

	private boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}
}
