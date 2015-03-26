package org.betarss.producer;

import java.util.List;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.utils.HttpUtils;
import org.betarss.utils.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public class UrlProducer implements Producer {

	@Override
	public HttpEntity<byte[]> produceAsHttpEntity(String title, List<Torrent> torrents, boolean magnet) throws Exception {
		String data = produce(title, torrents, magnet);
		return HttpUtils.httpEntity("text", "raw", data);
	}

	@Override
	public String produce(String title, List<Torrent> torrents, boolean magnet) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Torrent torrent : torrents) {
			String link = magnet && Strings.isNotEmpty(torrent.magnet) ? torrent.magnet : torrent.url;
			sb.append(link).append("\n");
		}
		return sb.toString();
	}

}
