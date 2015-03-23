package org.betarss.resource;

import java.util.List;

import org.betarss.app.BetarssService;
import org.betarss.app.BetaseriesService;
import org.betarss.domain.BetarssSearch;
import org.betarss.domain.BetaseriesSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.rss.RssProducer;
import org.betarss.utils.SSLCertificateUtils;
import org.betarss.utils.ShowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//TODO : P3 - donner la possibilité de récupérér la dernière log d'une recherche
public class BetarssResource {

	@Autowired
	private BetaseriesService betaseriesService;

	@Autowired
	private BetarssService betarssService;

	@Autowired
	private RssProducer jaxbRssProducer;

	@Autowired
	private ConfigurationService configurationService;

	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@RequestParam(required = true) String show, //
			@RequestParam(required = true) Integer season, //
			@RequestParam(required = false) String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet) throws Exception {

		//TODO : P2 - builder pour feed search avec validation
		BetarssSearch search = new BetarssSearch();
		search.showEpisode.show = show;
		search.showEpisode.season = season;
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

		List<Torrent<ShowEpisode>> torrents = betarssService.searchTorrents(search);
		return httpEntity(produceRss(search, torrents));
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

		List<Torrent<ShowEpisode>> torrents = betaseriesService.getTorrents(search);
		return httpEntity(produceRss(search, torrents));
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

	private String produceRss(BetarssSearch search, List<Torrent<ShowEpisode>> torrents) throws Exception {
		return jaxbRssProducer.produceRSS2(feedTitle(search), torrents, search.magnet);
	}

	private String produceRss(BetaseriesSearch search, List<Torrent<ShowEpisode>> torrents) throws Exception {
		return jaxbRssProducer.produceRSS2(feedTitle(search), torrents, search.magnet);
	}

	private String feedTitle(BetarssSearch search) {
		return ShowUtils.formatEpisodeUpperCase(search.showEpisode.show, search.showEpisode.season);
	}

	private String feedTitle(BetaseriesSearch search) {
		return search.login + "@betaseries.com";
	}

	static {
		SSLCertificateUtils.avoidHttpsErrors();
	}

}
