package org.betarss.resource;

import org.betarss.domain.BetarssSearch;
import org.betarss.domain.BetaseriesSearch;
import org.betarss.domain.Feed;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.feed.IRssProducer;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.search.BetarssService;
import org.betarss.search.BetaseriesService;
import org.betarss.utils.SSLCertificateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//TODO : P1 - classe ProviderDictionnary
//TODO : P3 - donner la possibilité de récupérér la dernière log d'une recherche
public class BetarssResource {

	@Autowired
	private BetaseriesService betaseriesService;

	@Autowired
	private BetarssService betarssService;

	@Autowired
	private IRssProducer jaxbRssProducer;

	@Autowired
	private ConfigurationService configurationService;

	//TODO : P2 - possibilité de ne pas préciser de saison pouvoir filtrer sur la saison et filtre à partir d'une saison (ex: à partir de 3 = !S01^!S02^!S3) 
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

		//TODO : P2 - builder pour feed search avec validation
		BetarssSearch search = new BetarssSearch();
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

		Feed feed = betarssService.search(search);
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

		BetaseriesSearch search = new BetaseriesSearch();
		search.login = login;
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

		Feed feed = betaseriesService.getFeed(search);
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

	static {
		SSLCertificateUtils.avoidHttpsErrors();
	}

}
