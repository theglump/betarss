package org.betarss.provider;

import static org.betarss.utils.Filters.joinFilterAnd;

import java.io.IOException;
import java.util.List;

import org.betarss.domain.BetarssSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Quality;
import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;

// TODO : to prototype ?
public class SearchEngine {

	private TorrentFilterer torrentFilterer;

	private Crawler crawler;

	private FilterComputor filterComputor;

	public SearchEngine(TorrentFilterer torrentFilterer, Crawler crawler, FilterComputor filterComputor) {
		this.torrentFilterer = torrentFilterer;
		this.crawler = crawler;
		this.filterComputor = filterComputor;
	}

	public List<Torrent> doSearch(BetarssSearch search) throws IOException, FeedFilterException {
		List<Torrent> torrents = crawler.doCrawl(search.showEpisode.show, search.showEpisode.season, search.backlink);
		String filter = computeFilter(search);
		return torrentFilterer.filter(torrents, filter);
	}

	private String computeFilter(BetarssSearch search) {
		String searchFilter = filterComputor.getFilter(getLanguage(search), getQuality(search));
		return joinFilterAnd(searchFilter, search.filter);
	}

	private Language getLanguage(BetarssSearch parameter) {
		return parameter.languages.size() > 0 ? parameter.languages.get(0) : null;
	}

	private Quality getQuality(BetarssSearch search) {
		return search.qualities.size() > 0 ? search.qualities.get(0) : null;
	}

}
