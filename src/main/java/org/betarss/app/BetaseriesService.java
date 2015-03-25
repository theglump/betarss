package org.betarss.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import org.betarss.provider.TorrentSearcherProvider;
import org.betarss.provider.TorrentSearcher;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Procedure;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

@Service
public class BetaseriesService {

	private static final String BETASERIES_FEED_URL = "https://www.betaseries.com/rss/planning/";
	private static final Pattern SEASON_PATTERN = Pattern.compile(".*S0?(\\d+).*");

	@Autowired
	private TorrentSearcherProvider torrentSearcherProvider;

	@Autowired
	private ConfigurationService configurationService;

	public List<Torrent<ShowEpisode>> getTorrents(final BetaseriesSearch search) throws IOException, FeedFilterException {
		final List<Torrent<ShowEpisode>> results = new CopyOnWriteArrayList<Torrent<ShowEpisode>>();
		final TorrentSearcher torrentSearcher = getFeedSearcher(search);

		SetMultimap<String, String> titlesBySeason = HashMultimap.create();
		for (String title : getItemTitles(search.login)) {
			String key = title.substring(0, title.length() - 4);
			titlesBySeason.put(key, title);
		}

		List<Procedure> procedures = Lists.newArrayList();
		for (String key : titlesBySeason.keys()) {
			final Set<String> titles = titlesBySeason.get(key);

			procedures.add(new Procedure() {

				@Override
				public void doCall() throws Exception {
					String first = titles.iterator().next();
					String showName = first.substring(0, first.length() - 7);
					BetarssSearch betarssSearch = new BetarssSearch(search);
					betarssSearch.showEpisode.show = showName;
					betarssSearch.showEpisode.season = getSeason(first);
					List<Torrent<ShowEpisode>> torrents = torrentSearcher.doSearch(betarssSearch);
					for (String title : titles) {
						for (Torrent<ShowEpisode> torrent : torrents) {
							if (torrent.title.startsWith((title))) {
								results.add(torrent);
							}
						}
					}
				}

			});
		}
		BetarssUtils.multiThreadCalls(procedures, 120);
		return results;
	}

	private List<String> getItemTitles(String login) throws IOException {
		List<String> titles = new ArrayList<String>();
		Elements elementsByTag = Jsoup.connect(BETASERIES_FEED_URL + login).get().getElementsByTag("title");
		Iterator<org.jsoup.nodes.Element> iterator = elementsByTag.iterator();
		iterator.next(); // skip global title
		while (iterator.hasNext()) {
			String rawTitle = iterator.next().text();
			titles.add(rawTitle.substring(9, rawTitle.length() - 3));
		}
		return titles;
	}

	private int getSeason(String str) {
		Matcher m = SEASON_PATTERN.matcher(str);
		return m.find() ? Integer.parseInt(m.group(1)) : 0;
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
