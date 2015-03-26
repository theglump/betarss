package org.betarss.producer;

import java.util.List;

import org.betarss.domain.Torrent;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.HttpUtils;
import org.betarss.utils.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

@Service
public class HtmlProducer implements Producer {

	@Override
	public HttpEntity<byte[]> produceAsHttpEntity(String title, List<Torrent> torrents, boolean magnet) throws Exception {
		String data = produce(title, torrents, magnet);
		return HttpUtils.httpEntity("text", "html", data);
	}

	@Override
	public String produce(String title, List<Torrent> torrents, boolean magnet) throws Exception {
		BetarssUtils.log("" + torrents.size());
		StringBuilder sb = new StringBuilder("<html><head></head><body><table>");
		for (Torrent torrent : torrents) {
			String link = magnet && Strings.isNotEmpty(torrent.magnet) ? torrent.magnet : torrent.url;
			sb.append("<tr><td><a href=\"").append(link).append("\">").append(torrent.title).append("</a></td></tr>");
		}
		return sb.append("</table></body></head>").toString();
	}
}
