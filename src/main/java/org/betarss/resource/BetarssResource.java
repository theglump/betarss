package org.betarss.resource;

import static org.betarss.utils.Shows.formatEpisodeUpperCase;

import java.util.List;

import org.betarss.app.BetarssService;
import org.betarss.app.BetaseriesService;
import org.betarss.domain.BaseSearch;
import org.betarss.domain.BetarssSearch;
import org.betarss.domain.BetaseriesSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Mode;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;
import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.producer.ProducerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mangofactory.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
//TODO : P3 - donner la possibilité de récupérér la dernière log d'une recherche
@RequestMapping({ "/api" })
@Api(value = "coco", description = "TV show torrents")
public class BetarssResource {

	@Autowired
	private BetaseriesService betaseriesService;

	@Autowired
	private BetarssService betarssService;

	@Autowired
	private ProducerProvider producerProvider;

	@Autowired
	private ConfigurationService configurationService;

	@ApiOperation(httpMethod = "get", value = "feed")
	@ApiModel(type = String.class)
	@RequestMapping(value = "feed", method = RequestMethod.GET)
	public HttpEntity<byte[]> feed( //
			@ApiParam(name = "show", required = true, internalDescription = "coco")//
			@RequestParam(required = true) String show, //
			@RequestParam(required = true) Integer season, //
			@RequestParam(required = false, defaultValue = "en") String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {

		BaseSearch baseSearch = computeBaseSearch(language, provider, quality, filter, date, magnet);
		BetarssSearch betarssSearch = new BetarssSearch(baseSearch);
		betarssSearch.showEpisode.show = show;
		betarssSearch.showEpisode.season = season;
		List<Torrent> torrents = betarssService.searchTorrents(betarssSearch);
		return produce(mode, torrents, betarssSearch);
	}

	@RequestMapping(value = "last", method = RequestMethod.GET)
	public HttpEntity<byte[]> last( //
			@RequestParam(required = false, defaultValue = "en") String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {
		return feed(null, -1, language, provider, quality, filter, date, magnet, mode);
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> betaseries( //
			@RequestParam(required = true) String login, //
			@RequestParam(required = false, defaultValue = "en") String language, //
			@RequestParam(required = false) String provider, //
			@RequestParam(required = false) String quality, //
			@RequestParam(required = false) String filter, //
			@RequestParam(required = false, defaultValue = "true") Boolean date, //
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {

		BaseSearch baseSearch = computeBaseSearch(language, provider, quality, filter, date, magnet);
		BetaseriesSearch betaseriesSearch = new BetaseriesSearch(baseSearch);
		betaseriesSearch.login = login;
		List<Torrent> torrents = betaseriesService.getTorrents(betaseriesSearch);
		return produce(mode, torrents, betaseriesSearch);
	}

	@RequestMapping(value = "search", method = RequestMethod.GET)
	public HttpEntity<byte[]> search( //
			@RequestParam(required = true) String hash) throws Exception {
		return null;
	}

	private HttpEntity<byte[]> produce(String mode, List<Torrent> torrents, BetarssSearch betarssSearch) throws Exception {
		return producerProvider.provide(getMode(mode)).produceAsHttpEntity(feedTitle(betarssSearch), torrents, betarssSearch.magnet);
	}

	private String feedTitle(BetarssSearch betarssSearch) {
		ShowEpisode showEpisode = betarssSearch.showEpisode;
		return showEpisode.show != null ? formatEpisodeUpperCase(showEpisode.show, showEpisode.season) : "Last entries...";
	}

	private HttpEntity<byte[]> produce(String mode, List<Torrent> torrents, BetaseriesSearch betaseriesSearch) throws Exception {
		return producerProvider.provide(getMode(mode)).produceAsHttpEntity(feedTitle(betaseriesSearch), torrents, betaseriesSearch.magnet);
	}

	private String feedTitle(BetaseriesSearch search) {
		return search.login + "'s feed @ betaseries.com";
	}

	private Mode getMode(String mode) {
		return Mode.parse(mode);
	}

	private BetarssSearch computeBaseSearch(String language, String provider, String quality, String filter, Boolean date, Boolean magnet) {
		BetarssSearch search = new BetarssSearch();
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
		return search;
	}

}
