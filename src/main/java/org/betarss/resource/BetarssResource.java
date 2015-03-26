package org.betarss.resource;

import static org.betarss.utils.Shows.formatEpisodeUpperCase;

import java.util.List;

import org.betarss.app.BetarssService;
import org.betarss.app.BetaseriesService;
import org.betarss.domain.BetarssSearch;
import org.betarss.domain.BetaseriesSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Mode;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.domain.Torrent;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.producer.ProducerProvider;
import org.betarss.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
	private ProducerProvider producerProvider;

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
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {

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

		List<Torrent> torrents = betarssService.searchTorrents(search);

		return produce(mode, torrents, feedTitle(search), magnet);
	}

	@RequestMapping(value = "last", method = RequestMethod.GET)
	public HttpEntity<byte[]> last( //
			@RequestParam(required = false) String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, @RequestParam(required = false, defaultValue = "rss") String mode)
			throws Exception {
		return feed(null, -1, language, provider, quality, filter, date, magnet, mode);
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> betaseries( //
			@RequestParam(required = true) String login, //
			@RequestParam(required = false) String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {

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

		List<Torrent> torrents = betaseriesService.getTorrents(search);

		return produce(mode, torrents, feedTitle(search), magnet);
	}

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public HttpEntity<byte[]> search( //
			@RequestParam(required = true) String hash) throws Exception {
		return null;
	}

	private HttpEntity<byte[]> produce(String mode, List<Torrent> torrents, String title, boolean magnet) throws Exception {
		return producerProvider.provide(getMode(mode)).produceAsHttpEntity(title, torrents, magnet);
	}

	private String feedTitle(BetarssSearch search) {
		return search.showEpisode.show != null ? formatEpisodeUpperCase(search.showEpisode.show, search.showEpisode.season) : "Last entries...";
	}

	private String feedTitle(BetaseriesSearch search) {
		return search.login + "@betaseries.com";
	}

	private Mode getMode(String mode) {
		return Mode.parse(mode);
	}

	static {
		HttpUtils.avoidHttpsErrors();
	}

}
