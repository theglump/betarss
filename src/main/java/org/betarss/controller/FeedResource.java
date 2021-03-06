package org.betarss.controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mangofactory.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping({ "/feed" })
@Api(value = "feed", description = "Torrents feeds for tv shows")
public class FeedResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeedResource.class);

	@Autowired
	private BetaseriesService betaseriesService;

	@Autowired
	private BetarssService betarssService;

	@Autowired
	private ProducerProvider producerProvider;

	@Autowired
	private ConfigurationService configurationService;

	@ApiModel(type = String.class)
	@RequestMapping(value = "show", method = RequestMethod.GET)
	public HttpEntity<byte[]> specificShow( //
			@ApiParam(name = "show", required = true)//
			@RequestParam(required = true) String show, //
			@ApiParam(name = "season", required = true)//
			@RequestParam(required = true) Integer season, //
			@ApiParam(name = "language", required = true, allowableValues = "en,vostfr,fr")//
			@RequestParam(required = false, defaultValue = "en") String language, //
			@ApiParam(name = "provider", required = false, allowableValues = "eztv,cpasbien,showrss,kickass")//
			@RequestParam(required = false) String provider, //
			@ApiParam(name = "quality", allowableValues = "sd,hd")//
			@RequestParam(required = false) String quality, //
			@ApiParam(name = "filter")//
			@RequestParam(required = false) String filter, //
			@ApiParam(name = "magnet", defaultValue = "true")//
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "false") Boolean backlink, //
			@ApiParam(name = "mode", required = true, allowableValues = "html,rss,url")//
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {

		BetarssSearch betarssSearch = computeBetarssSearch(show, season, language, provider, quality, filter, magnet, backlink);
		LOGGER.info("Betarss search for : " + betarssSearch);
		List<Torrent> torrents = betarssService.searchTorrents(betarssSearch);
		return produce(mode, torrents, betarssSearch);
	}

	@RequestMapping(value = "betaseries", method = RequestMethod.GET)
	public HttpEntity<byte[]> fromBetaseries( //
			@ApiParam(name = "login", required = true)//
			@RequestParam(required = true) String login, //
			@ApiParam(name = "language", required = true, allowableValues = "en,vostfr,fr")//
			@RequestParam(required = false, defaultValue = "en") String language, //
			@ApiParam(name = "provider", required = false, allowableValues = "eztv,cpasbien,showrss,kickass")//
			@RequestParam(required = false) String provider, //
			@ApiParam(name = "quality", allowableValues = "sd,hd")//
			@RequestParam(required = false) String quality, //
			@ApiParam(name = "filter")//
			@RequestParam(required = false) String filter, //
			@ApiParam(name = "magnet", defaultValue = "true")//
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "false") Boolean backlink, //
			@ApiParam(name = "mode", required = true, allowableValues = "html,rss,url")//
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {

		BetaseriesSearch betaseriesSearch = getBetaseriesSearch(login, language, provider, quality, filter, magnet, backlink);
		LOGGER.info("Betaseries search for : " + betaseriesSearch);
		List<Torrent> torrents = betaseriesService.getTorrents(betaseriesSearch);
		return produce(mode, torrents, betaseriesSearch);
	}

	//@ApiIgnore
	@RequestMapping(value = "latest", method = RequestMethod.GET)
	public HttpEntity<byte[]> latestEpisodes( //
			@ApiParam(name = "language", required = true, allowableValues = "en,vostfr,fr")//
			@RequestParam(required = false, defaultValue = "en") String language, //
			@ApiParam(name = "provider", required = false, allowableValues = "eztv,cpasbien,showrss,kickass")//
			@RequestParam(required = false) String provider, //
			@ApiParam(name = "quality", allowableValues = "sd,hd")//
			@RequestParam(required = false) String quality, //
			@ApiParam(name = "filter")//
			@RequestParam(required = false) String filter, //
			@ApiParam(name = "date", defaultValue = "true")//
			@RequestParam(required = false, defaultValue = "true") Boolean magnet, //
			@RequestParam(required = false, defaultValue = "false") Boolean backlink, //
			@ApiParam(name = "mode", required = true, allowableValues = "html,rss,url")//
			@RequestParam(required = false, defaultValue = "rss") String mode) throws Exception {
		return specificShow(null, -1, language, provider, quality, filter, magnet, backlink, mode);
	}

	private HttpEntity<byte[]> produce(String mode, List<Torrent> torrents, BetarssSearch betarssSearch) throws Exception {
		return producerProvider.provide(getMode(mode)).produceAsHttpEntity(feedTitle(betarssSearch), feedDescription(betarssSearch), torrents,
				betarssSearch.magnet);
	}

	private String feedTitle(BetarssSearch betarssSearch) {
		ShowEpisode showEpisode = betarssSearch.showEpisode;
		if (showEpisode.show != null) {
			return "[BetaRss] " + formatEpisodeUpperCase(showEpisode.show, showEpisode.season);
		}
		return "[BetaRss] Last entries";
	}

	private HttpEntity<byte[]> produce(String mode, List<Torrent> torrents, BetaseriesSearch betaseriesSearch) throws Exception {
		return producerProvider.provide(getMode(mode)).produceAsHttpEntity(feedTitle(betaseriesSearch), feedDescription(betaseriesSearch), torrents,
				betaseriesSearch.magnet);
	}

	private String feedTitle(BetaseriesSearch search) {
		return "[BetaRss] Feed for " + search.login + "@betaseries.com";
	}

	private String feedDescription(BaseSearch search) {
		return search.toString();
	}

	private Mode getMode(String mode) {
		return Mode.parse(mode);
	}

	private BetarssSearch computeBetarssSearch(String show, Integer season, String language, String provider, String quality, String filter,
			Boolean magnet, Boolean backlink) {
		BaseSearch baseSearch = computeBaseSearch(language, provider, quality, filter, magnet, backlink);
		BetarssSearch betarssSearch = new BetarssSearch(baseSearch);
		betarssSearch.showEpisode.show = show;
		betarssSearch.showEpisode.season = season;
		return betarssSearch;
	}

	private BetaseriesSearch getBetaseriesSearch(String login, String language, String provider, String quality, String filter, Boolean magnet,
			Boolean backlink) {
		BaseSearch baseSearch = computeBaseSearch(language, provider, quality, filter, magnet, backlink);
		BetaseriesSearch betaseriesSearch = new BetaseriesSearch(baseSearch);
		betaseriesSearch.login = login;
		return betaseriesSearch;
	}

	private BetarssSearch computeBaseSearch(String language, String provider, String quality, String filter, Boolean magnet, Boolean backlink) {
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
		search.date = true;
		search.backlink = backlink;
		return search;
	}

}
