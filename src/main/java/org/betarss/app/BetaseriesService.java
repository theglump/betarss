package org.betarss.app;

import static org.betarss.utils.Shows.createShowEpisode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.BaseSearch;
import org.betarss.domain.BetarssSearch;
import org.betarss.domain.BetaseriesSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.infrastructure.http.HttpClient;
import org.betarss.provider.SearchEngine;
import org.betarss.provider.SearchEngineProvider;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Procedure;
import org.betarss.utils.Shows;
import org.betarss.utils.Torrents;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class BetaseriesService {

	private static final String BETASERIES_FEED_URL = "https://www.betaseries.com/rss/planning/";
	private static final Pattern SEASON_PATTERN = Pattern.compile("(.*) S0?(\\d+).*");

	@Autowired
	private SearchEngineProvider searchEngineProvider;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	@Qualifier("httpClient")
	private HttpClient httpClient;

	public List<Torrent> getTorrents(final BetaseriesSearch betaseriesSearch) throws IOException, FeedFilterException {
		final List<Torrent> results = new CopyOnWriteArrayList<Torrent>();
		Map<String, List<String>> itemsByShow = itemByShows(betaseriesSearch);
		List<Procedure> procedures = getProcedures(betaseriesSearch, results, itemsByShow);
		BetarssUtils.multiThreadCalls(procedures, 120);
		return Torrents.sort(results);
	}

	private List<Procedure> getProcedures(final BetaseriesSearch betaseriesSearch, final List<Torrent> results,
			Map<String, List<String>> entriesByShow) {
		List<Procedure> procedures = Lists.newArrayList();

		for (String showName : entriesByShow.keySet()) {
			final List<String> entries = entriesByShow.get(showName);

			procedures.add(new Procedure() {

				@Override
				public void doCall() throws Exception {
					BetarssSearch betarssSearch = createBetarssSearch(betaseriesSearch, transcodeItem(entries.get(0)));
					List<Torrent> doSearch = getSearchEngine(betaseriesSearch).doSearch(betarssSearch);
					for (String entry : entries) {
						for (Torrent torrent : doSearch) {
							if (torrent.getShowEpisode().equals(createShowEpisode(entry))) {
								results.add(torrent);
							}
						}
					}
				}

			});
		}
		return procedures;
	}

	private BetarssSearch createBetarssSearch(final BetaseriesSearch search, String item) {
		BetarssSearch betarssSearch = new BetarssSearch(search);
		betarssSearch.showEpisode = Shows.createShowEpisode(item);
		return betarssSearch;
	}

	private Map<String, List<String>> itemByShows(final BetaseriesSearch search) throws IOException {
		Map<String, List<String>> itemsByShow = Maps.newHashMap();
		for (String item : getBetaseriesItems(search.login)) {
			String showName = getShowName(item);
			List<String> items = itemsByShow.get(showName);
			if (items == null) {
				items = Lists.newArrayList();
				itemsByShow.put(showName, items);
			}
			items.add(item);
		}
		return itemsByShow;
	}

	private List<String> getBetaseriesItems(String login) throws IOException {
		List<String> results = Lists.newArrayList();
		String betaseriesFeed = httpClient.get(BETASERIES_FEED_URL + login);
		for (Element element : Jsoup.parse(betaseriesFeed).getElementsByTag("title")) {
			results.add(element.text());
		}
		List<String> items = Lists.newArrayList();
		for (String rawTitle : results.subList(1, results.size())) {
			items.add(rawTitle.substring(9, rawTitle.length() - 3));
		}
		return items;
	}

	private String getShowName(String str) {
		Matcher m = SEASON_PATTERN.matcher(str);
		return m.find() ? m.group(1) : "";
	}

	private SearchEngine getSearchEngine(BaseSearch betarssSearch) {
		Provider provider = getProvider(betarssSearch);
		return searchEngineProvider.get(provider);
	}

	private Provider getProvider(BaseSearch baseSearch) {
		if (baseSearch.languages.size() > 0) {
			return configurationService.getBetaseriesProviders().get(getLanguage(baseSearch)).get(0);
		}
		return baseSearch.providers.get(0);
	}

	private Language getLanguage(BaseSearch parameter) {
		return parameter.languages.size() > 0 ? parameter.languages.get(0) : null;
	}

	private String transcodeItem(String item) {
		if (item.startsWith("The Royals (2015)")) {
			return item.replace("The Royals (2015)", "The Royals");
		}
		return item;
	}
}
