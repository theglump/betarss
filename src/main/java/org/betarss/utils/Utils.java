package org.betarss.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.betarss.exception.BetarssException;

public class Utils {

	public interface Try<T> {
		T doTry() throws Exception;
	}

	public static <T> T doTry(int max, Try<T> t) {
		int times = 0;
		while (true) {
			try {
				return t.doTry();
			} catch (Exception e) {
				if (++times == max) {
					throw new BetarssException(e);
				}
			}
		}
	}

	public static InputStream getUrlDataInputStream(String url) throws MalformedURLException, IOException {
		HttpURLConnection connection = openConnection(url);
		int responseCode = connection.getResponseCode();
		while ((responseCode / 100) == 3) {
			connection = openConnection(connection.getHeaderField("Location"));
			responseCode = connection.getResponseCode();
		}
		return connection.getInputStream();
	}

	private static HttpURLConnection openConnection(String location) throws MalformedURLException, IOException {
		URL url = new URL(location);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		return connection;
	}

	static {
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
