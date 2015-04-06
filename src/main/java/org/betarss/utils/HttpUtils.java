package org.betarss.utils;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpUtils {

	public static HttpEntity<byte[]> httpEntity(String type, String subType, String data) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType(type, subType));
		header.setContentLength(data.length());
		return new HttpEntity<byte[]>(data.getBytes(), header);
	}

	public static HttpEntity<byte[]> httpEntity(String type, String subType, String filename, byte[] bytes) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType(type, subType));
		header.setContentLength(bytes.length);
		header.setContentDispositionFormData("attachment", filename);
		return new HttpEntity<byte[]>(bytes, header);
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
