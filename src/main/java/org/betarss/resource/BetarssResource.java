package org.betarss.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.betarss.core.BetaseriesFeedProducer;
import org.betarss.core.CrawlerProvider;
import org.betarss.core.FeedFilter;
import org.betarss.core.ICrawler;
import org.betarss.core.IRssProducer;
import org.betarss.domain.Feed;
import org.betarss.domain.Language;
import org.betarss.utils.Utils;
import org.hsqldb.lib.StringInputStream;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.io.ByteStreams;

@Controller
public class BetarssResource {

	static {
		Class u = Utils.class;
	}

	@Autowired
	private CrawlerProvider crawlerProvider;

	@Autowired
	private FeedFilter feedFilter;

	@Autowired
	private IRssProducer jaxbRssProducer;

	@Autowired
	private BetaseriesFeedProducer betaseriesFeedProducer;

	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@RequestParam(required = true) String show, //
			@RequestParam(required = true) int season, //
			@RequestParam(required = false, defaultValue = "vostfr") String language, //
			@RequestParam(required = false) String filter) throws Exception {

		Language lang = Language.parse(language);
		Feed feed = getCrawler(lang).getFeed(show, season);
		feed = feedFilter.filter(feed, filter, lang);

		return httpEntity(produceRss(feed));
	}

	@RequestMapping(value = "last", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@RequestParam(required = false, defaultValue = "vostfr") String language) throws Exception {

		Language lang = Language.parse(language);
		Feed feed = getCrawler(lang).getFeed();
		feed = feedFilter.filter(feed, null, lang);

		return httpEntity(produceRss(feed));
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> betaseries( //
			@RequestParam(required = true) String login, //
			@RequestParam(required = false, defaultValue = "vostfr") String language, //
			@RequestParam(required = false) String filter) throws Exception {

		Language lang = Language.parse(language);
		Feed feed = betaseriesFeedProducer.getFeed(getCrawler(lang), login);
		feed = feedFilter.filter(feed, filter, lang);

		return httpEntity(produceRss(feed));
	}

	@RequestMapping(value = "torrent", method = RequestMethod.GET)
	@ResponseBody
	public void getFile(@RequestParam String location, HttpServletResponse response) throws IOException {
		String content = Jsoup.connect(location).ignoreContentType(true).get().body().text();
		response.setHeader("Content-Disposition", " attachment; filename=" + location.hashCode() + ".torrent");
		ByteStreams.copy(new StringInputStream(content), response.getOutputStream());
		response.flushBuffer();
	}

	private ICrawler getCrawler(Language language) {
		return crawlerProvider.provide(language);
	}

	private String produceRss(Feed feed) throws Exception {
		return jaxbRssProducer.produceRSS2(feed);
	}

	private HttpEntity<byte[]> httpEntity(String xml) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "xml"));
		header.setContentLength(xml.length());
		return new HttpEntity<byte[]>(xml.getBytes(), header);
	}

}
