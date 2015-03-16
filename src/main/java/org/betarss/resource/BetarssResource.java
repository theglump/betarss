package org.betarss.resource;

import org.betarss.core.business.IFeedService;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.feed.IRssProducer;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.utils.Utils;
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

	static {
		Class u = Utils.class;
	}

	@Autowired
	private IFeedService feedService;

	@Autowired
	private IRssProducer jaxbRssProducer;

	@Autowired
	private ConfigurationService configurationService;

	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@RequestParam(required = true) String show, //
			@RequestParam(required = true) int season, //
			@RequestParam(required = false) String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet) throws Exception {

		FeedSearch search = new FeedSearch();
		search.show = show;
		search.season = season;
		if (provider != null) {
			search.providers.add(Provider.parse(provider));
		} else {
			search.languages.add(Language.parse(language));
		}
		if (quality != null) {
			search.qualities.add(Quality.parse(quality));
		}
		search.filter = filter;
		search.magnet = magnet;
		search.date = date;

		Feed feed = feedService.getFeed(search);
		return httpEntity(produceRss(feed));
	}

	@RequestMapping(value = "last", method = RequestMethod.GET)
	public HttpEntity<byte[]> last( //
			@RequestParam(required = false) String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet) throws Exception {
		return feed(null, -1, language, provider, quality, filter, date, magnet);
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> betaseries( //
			@RequestParam(required = true) String login, //
			@RequestParam(required = false) String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet) throws Exception {

		FeedSearch search = new FeedSearch();
		if (provider != null) {
			search.providers.add(Provider.parse(provider));
		} else {
			search.languages.add(Language.parse(language));
		}
		if (quality != null) {
			search.qualities.add(Quality.parse(quality));
		}
		search.filter = filter;
		search.magnet = magnet;
		search.date = date;

		Feed feed = feedService.getFeed(search);
		return httpEntity(produceRss(feed));
	}

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public HttpEntity<byte[]> search( //
			@RequestParam(required = true) String hash) throws Exception {
		return null;
	}

	private HttpEntity<byte[]> httpEntity(String xml) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "xml"));
		header.setContentLength(xml.length());
		return new HttpEntity<byte[]>(xml.getBytes(), header);
	}

	private String produceRss(Feed feed) throws Exception {
		return jaxbRssProducer.produceRSS2(feed);
	}
	//	@RequestMapping(value = "torrent", method = RequestMethod.GET)
	//	@ResponseBody
	//	public void torrent(@RequestParam String location, HttpServletResponse response) throws IOException {
	//		String content = Jsoup.connect(location).ignoreContentType(true).get().body().text();
	//		response.setHeader("Content-Disposition", " attachment; filename=" + location.hashCode() + ".torrent");
	//		ByteStreams.copy(new StringInputStream(content), response.getOutputStream());
	//		response.flushBuffer();
	//	}
}
