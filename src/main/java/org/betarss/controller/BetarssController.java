package org.betarss.controller;

import org.betarss.core.ICrawler;
import org.betarss.core.FeedFilter;
import org.betarss.core.RssProducer;
import org.betarss.domain.Feed;
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
	private ICrawler cpasbienCrawler;
	
	@Autowired
	private FeedFilter feedFilter;
	
	@Autowired
	private RssProducer rssProducer;
	
	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> get( //
			@RequestParam(required=true) String show, //
			@RequestParam(required=true) int season, //
			@RequestParam(required=false, defaultValue="vostfr") String language, //
			@RequestParam(required=false) String filter) throws Exception {
		
		Feed feed = cpasbienCrawler.getFeed(show, season);

		if (!isEmpty(filter)) {
			feed = feedFilter.filter(feed, filter);
		}
	    
		return httpEntity(rssProducer.produceRSS2(feed));
	}

	private HttpEntity<byte[]> httpEntity(String xml) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "xml"));
		header.setContentLength(xml.length());
		return new HttpEntity<byte[]>(xml.getBytes(), header);
	}
	
	private boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

}
