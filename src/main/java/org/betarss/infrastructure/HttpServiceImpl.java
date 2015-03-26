package org.betarss.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.betarss.exception.BetarssException;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Function;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

@Service
public class HttpServiceImpl implements HttpService {

	private static final String HTTP_USER_AGENT = "Mozilla/5.0";

	@Override
	public String get(String url) {
		try {
			return getDocument(url).html();
		} catch (IOException e) {
			throw new BetarssException(e);
		}
	}

	@Override
	public List<String> getTags(String url, String tagName) {
		List<String> results = Lists.newArrayList();
		try {
			for (Element element : getDocument(url).getElementsByTag(tagName)) {
				results.add(element.text());
			}
		} catch (IOException e) {
			throw new BetarssException(e);
		}
		return results;
	}

	@Override
	public String get(final String url, int maxTime) {
		return BetarssUtils.doTry(maxTime, new Function<String>() {

			@Override
			public String doCall() throws Exception {
				return get(url);
			}
		});
	}

	@Override
	public String post(final String url, final Parameter... parameters) {
		return postDocument(url, parameters).html();
	}

	@Override
	public String post(final String url, int maxTime, final Parameter... parameters) {
		return BetarssUtils.doTry(maxTime, new Function<String>() {

			@Override
			public String doCall() throws Exception {
				return post(url, parameters);
			}
		});

	}

	@Override
	public byte[] getData(String url) throws IOException {
		URL u = new URL(url);
		HttpURLConnection http = (HttpURLConnection) u.openConnection();
		Map<String, List<String>> header = http.getHeaderFields();
		while (isRedirected(header)) {
			url = header.get("Location").get(0);
			u = new URL(url);
			http = (HttpURLConnection) u.openConnection();
			header = http.getHeaderFields();
		}
		InputStream input = http.getInputStream();
		return ByteStreams.toByteArray(input);
	}

	@Override
	public String dataAsString(String url) throws IOException {
		return new String(getData(url), Charsets.UTF_8);
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).userAgent(HTTP_USER_AGENT).get();
	}

	private Document postDocument(String url, Parameter... parameters) {
		Connection connection = Jsoup.connect(url).userAgent(HTTP_USER_AGENT);
		for (Parameter parameter : parameters) {
			connection = connection.data(parameter.name, parameter.value);
		}
		try {
			return connection.post();
		} catch (IOException e) {
			throw new BetarssException(e);
		}
	}

	private boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 "))
				return true;
		}
		return false;
	}

	public static class Parameter {
		private String name;
		private String value;

		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public static Parameter create(String name, String value) {
			return new Parameter(name, value);
		}
	}
}
