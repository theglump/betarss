package org.betarss.producer;

import java.util.List;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.springframework.http.HttpEntity;

public interface Producer {

	public String produce(String title, List<Torrent<ShowEpisode>> torrents, boolean magnet) throws Exception;

	public HttpEntity<byte[]> produceAsHttpEntity(String title, List<Torrent<ShowEpisode>> torrents, boolean magnet) throws Exception;
}
