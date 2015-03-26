package org.betarss.infrastructure.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.betarss.exception.BetarssException;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.utils.BetarssUtils;
import org.betarss.utils.BetarssUtils.Function;
import org.betarss.utils.HttpUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

@Service
public class NetHttpClient implements HttpClient {

	private static final String HTTP_USER_AGENT = "Mozilla/5.0";
	private static final boolean SERIALIZATION = true;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public String get(String url) {
		try {
			String html = getDocument(url).html();
			if (SERIALIZATION) {
				serialize(url, html);
			}
			return html;
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
			if (SERIALIZATION) {
				serializeTag(url, tagName, results);
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
		String html = postDocument(url, parameters).html();
		if (SERIALIZATION) {
			serialize(url, html, parameters);
		}
		return html;
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
	public byte[] getData(String url) {
		try {
			HttpURLConnection http = openConnection(url);
			InputStream input = http.getInputStream();
			byte[] data = ByteStreams.toByteArray(input);
			if (SERIALIZATION) {
				serialize(url, data);
			}
			return data;
		} catch (MalformedURLException e) {
			throw new BetarssException("error during data fetching for " + url, e);
		} catch (IOException e) {
			throw new BetarssException("error during data fetching for " + url, e);
		}

	}

	@Override
	public String dataAsString(String url) {
		return new String(getData(url), Charsets.UTF_8);
	}

	private HttpURLConnection openConnection(String url) throws MalformedURLException, IOException {
		HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
		Map<String, List<String>> header = http.getHeaderFields();
		while (isRedirected(header)) {
			url = header.get("Location").get(0);
			http = (HttpURLConnection) new URL(url).openConnection();
			header = http.getHeaderFields();
		}
		return http;
	}

	private boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 "))
				return true;
		}
		return false;
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

	private void serialize(String url, byte[] data) {
		serialize(url, new String(data, Charsets.UTF_8), (Parameter[]) null);
	}

	private void serialize(String url, String html, Parameter... parameters) {
		try {
			HttpUtils.serializeRequest(configurationService.getHttpSerializationDirectory(), url, html, parameters);
		} catch (IOException e) {
			throw new BetarssException("error during serialization " + url, e);
		}
	}

	private void serializeTag(String url, String tagName, List<String> items) {
		try {
			HttpUtils.serializeTagRequest(configurationService.getHttpSerializationDirectory(), url, tagName, items);
		} catch (IOException e) {
			throw new BetarssException("error during serialization " + url, e);
		}
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
