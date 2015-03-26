package org.betarss.utils;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.betarss.exception.BetarssException;
import org.betarss.infrastructure.http.NetHttpClient.Parameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

public class HttpUtils {

	public static void serializeRequest(String directory, String url, String data) throws IOException {
		serializeRequest(directory, url, data, (Parameter[]) null);
	}

	public static void serializeRequest(String directory, String url, String data, Parameter... parameters) throws IOException {
		File f = computeDataFile(directory, url, parameters);
		if (!f.exists()) {
			Files.createParentDirs(f);
		}

		Files.write(data.getBytes(), f);
	}

	public static void serializeTagRequest(String directory, String url, String tag, List<String> data) throws IOException {
		File f = computeTagDataFile(directory, url, tag);
		if (!f.exists()) {
			Files.createParentDirs(f);
		}
		Files.write(Joiner.on("µµµ").join(data).getBytes(), f);
	}

	public static String deserializeRequest(String directory, String url) throws IOException {
		return deserializeRequest(directory, url);
	}

	public static String deserializeRequest(String directory, String url, Parameter... parameters) throws IOException {
		File f = computeDataFile(directory, url, parameters);
		return Files.toString(f, Charsets.UTF_8);
	}

	public static List<String> deserializeTagRequest(String directory, String url, String tag) throws IOException {
		File f = computeTagDataFile(directory, url, tag);
		String data = Files.toString(f, Charsets.UTF_8);
		return Splitter.on("µµµ").splitToList(data);
	}

	private static File computeTagDataFile(String directory, String url, String tag) {
		return computeDataFile(directory, url + "_tag_" + tag);
	}

	private static File computeDataFile(String directory, String url, Parameter... parameters) {
		Pattern p = Pattern.compile(".*:\\/\\/(((?!\\/).)*)(\\/(.*))?");
		Matcher m = p.matcher(url);
		if (m.find()) {
			String baseUrl = format(m.group(1));
			String pageUrl = Strings.defaultString(format(m.group(4)), "root");
			if (parameters != null) {
				for (Parameter parameter : parameters) {
					pageUrl += parameter.getName() + "_" + format(parameter.getValue());
				}
			}
			return new File(concat(directory, baseUrl, pageUrl) + ".html");
		}
		throw new BetarssException("Malformed url " + url);

	}

	private static String format(String elem) {
		if (Strings.isEmpty(elem)) {
			return "";
		}
		return elem.replaceAll("(\\.|\\/| )", "_");
	}

	private static String concat(String... elems) {
		StringBuilder sb = new StringBuilder();
		for (String elem : elems) {
			if (sb.length() != 0) {
				sb.append(File.separator);
			}
			sb.append(elem);
		}
		return sb.toString();
	}

	public static HttpEntity<byte[]> httpEntity(String type, String subType, String data) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType(type, subType));
		header.setContentLength(data.length());
		return new HttpEntity<byte[]>(data.getBytes(), header);
	}

	public static void avoidHttpsErrors() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}

			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
		}
	}
}
