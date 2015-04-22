package org.betarss.controller;

import static org.betarss.utils.HttpUtils.httpEntity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.betarss.infrastructure.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TorrentResource {

	@Autowired
	private ConfigurationService configurationService;

	@RequestMapping(value = "torrent", method = RequestMethod.GET)
	public HttpEntity<byte[]> torrent( //
			@RequestParam(required = true) String location) throws Exception {
		return httpEntity("application", "x-bittorrent", extracted(location), dataAt(location));
	}

	private String extracted(String location) {
		return location.hashCode() + ".torrent";
	}

	public byte[] dataAt(String location) throws Exception {
		URL url = new URL(location);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		Map<String, List<String>> header = http.getHeaderFields();
		while (isRedirected(header)) {
			location = header.get("Location").get(0);
			url = new URL(location);
			http = (HttpURLConnection) url.openConnection();
			header = http.getHeaderFields();
		}
		InputStream input = http.getInputStream();
		byte[] buffer = new byte[4096];
		int n = -1;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
		}
		output.close();
		return output.toByteArray();
	}

	private boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 "))
				return true;
		}
		return false;
	}

}
