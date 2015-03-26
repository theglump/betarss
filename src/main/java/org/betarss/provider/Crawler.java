package org.betarss.provider;

import java.util.List;

import org.betarss.domain.Torrent;

public interface Crawler {
	List<Torrent> doCrawl(String show, Integer season);
}
