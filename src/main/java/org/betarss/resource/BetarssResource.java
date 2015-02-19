package org.betarss.resource;

import java.io.IOException;

import org.betarss.core.BSFeedProducer;
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
public class BetarssResource {

	@Autowired
	private CrawlerProvider crawlerProvider;

	@Autowired
	private FeedFilter feedFilter;

	@Autowired
	private RssProducer rssProducer;

	@Autowired
	private BSFeedProducer bsFeedProducer;

	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@RequestParam(required = true) String show, //
			@RequestParam(required = true) int season, //
			@RequestParam(required = false, defaultValue = "vostfr") String language, //
			@RequestParam(required = false) String filter) throws Exception {

		Language lang = Language.get(language);
		Feed feed = getCrawler(lang).getFeed(show, season);
		feed = feedFilter.filter(feed, filter, lang);

		return httpEntity(produceRss(feed));
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> betaseries( //
			@RequestParam(required = true) String login, //
			@RequestParam(required = false, defaultValue = "vostfr") String language, //
			@RequestParam(required = false) String filter) throws Exception {

		Language lang = Language.get(language);
		Feed feed = bsFeedProducer.getFeed(getCrawler(lang), login);
		feed = feedFilter.filter(feed, filter, lang);

		return httpEntity(produceRss(feed));
	}

	private ICrawler getCrawler(Language language) {
		return crawlerProvider.provide(language);
	}

	private String produceRss(Feed feed) throws IOException {
		return rssProducer.produceRSS2(feed);
	}

	private HttpEntity<byte[]> httpEntity(String xml) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "xml"));
		header.setContentLength(xml.length());
		return new HttpEntity<byte[]>(xml.getBytes(), header);
	}
}
