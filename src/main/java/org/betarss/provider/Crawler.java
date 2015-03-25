package org.betarss.provider;

import java.io.IOException;
import java.util.List;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;

public interface Crawler {
	List<Torrent<ShowEpisode>> doCrawl(String show, Integer season) throws IOException, FeedFilterException;
}
