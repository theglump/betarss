package org.betarss.app;

import static org.betarss.utils.ShowUtils.createShowEpisode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.provider.TorrentSearcher;
import org.betarss.provider.TorrentSearcherProvider;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Procedure;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class BetaseriesService {

	private static final String BETASERIES_FEED_URL = "https://www.betaseries.com/rss/planning/";
	private static final Pattern SEASON_PATTERN = Pattern.compile("(.*) S0?(\\d+).*");

	@Autowired
	private TorrentSearcherProvider torrentSearcherProvider;

	@Autowired
	private ConfigurationService configurationService;

	public List<Torrent<ShowEpisode>> getTorrents(final BetaseriesSearch betaseriesSearch) throws IOException, FeedFilterException {
		final List<Torrent<ShowEpisode>> results = new CopyOnWriteArrayList<Torrent<ShowEpisode>>();
		Map<String, List<String>> itemsByShow = itemByShows(betaseriesSearch);
		List<Procedure> procedures = getProcedures(betaseriesSearch, results, itemsByShow);
		BetarssUtils.multiThreadCalls(procedures, 120);
		return orderResults(results);
	}

	private List<Procedure> getProcedures(final BetaseriesSearch betaseriesSearch, final List<Torrent<ShowEpisode>> results,
			Map<String, List<String>> entriesByShow) {
		List<Procedure> procedures = Lists.newArrayList();

		for (String showName : entriesByShow.keySet()) {
			final List<String> entries = entriesByShow.get(showName);

			procedures.add(new Procedure() {

				@Override
				public void doCall() throws Exception {
					BetarssSearch betarssSearch = createBetarssSearch(betaseriesSearch, entries.get(0));
					for (String entry : entries) {
						for (Torrent<ShowEpisode> torrent : getFeedSearcher(betaseriesSearch).doSearch(betarssSearch)) {
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
		betarssSearch.showEpisode = ShowUtils.createShowEpisode(item);
		return betarssSearch;
	}

	private List<Torrent<ShowEpisode>> orderResults(List<Torrent<ShowEpisode>> results) {
		Collections.sort(results, new Comparator<Torrent<ShowEpisode>>() {

			@Override
			public int compare(Torrent<ShowEpisode> e1, Torrent<ShowEpisode> e2) {
				return e1.title.compareTo(e2.title);
			}
		});
		return results;
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
		List<String> items = new ArrayList<String>();
		Elements elementsByTag = Jsoup.connect(BETASERIES_FEED_URL + login).get().getElementsByTag("title");
		Iterator<org.jsoup.nodes.Element> iterator = elementsByTag.iterator();
		iterator.next(); // skip global title
		while (iterator.hasNext()) {
			String rawTitle = iterator.next().text();
			items.add(rawTitle.substring(9, rawTitle.length() - 3));
		}
		return items;
	}

	private String getShowName(String str) {
		Matcher m = SEASON_PATTERN.matcher(str);
		return m.find() ? m.group(1) : "";
	}

	private TorrentSearcher getFeedSearcher(BaseSearch betarssSearch) {
		Provider provider = getProvider(betarssSearch);
		return torrentSearcherProvider.get(provider);
	}

	private Provider getProvider(BaseSearch baseSearch) {
		if (baseSearch.languages.size() > 0) {
			return configurationService.getProviders().get(getLanguage(baseSearch)).get(0);
		}
		return baseSearch.providers.get(0);
	}

	private Language getLanguage(BaseSearch parameter) {
		return parameter.languages.size() > 0 ? parameter.languages.get(0) : null;
	}
}
