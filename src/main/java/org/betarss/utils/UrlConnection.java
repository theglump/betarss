package org.betarss.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class UrlConnection {

	private static final String USER_AGENT = "Mozilla/5.0";
	private static final String POST_URL = "http://forum.wawa-mania.ec/login.php?action=in";
	private static final String REFERER_URL = "http://forum.wawa-mania.ec/login.php";
	private static final String HOST_URL = "forum.wawa-mania.ec";

	private boolean logged = false;
	private List<String> cookies = Lists.newArrayList();

	public UrlConnection() {
		CookieHandler.setDefault(new CookieManager());
	}

	public boolean login(String user, String password) throws IOException {
		_getPageContent(REFERER_URL);
		String post = sendPost(POST_URL, "req_username=" + user + "&req_password=" + password + "&form_sent=1&redirect_url=index.php");
		logged = post.contains("Vous êtes maintenant identifié");
		return logged;
	}

	public String getPageContent(String url) throws IOException {
		Preconditions.checkArgument(logged, "You must log in before");
		return _getPageContent(url);
	}

	private String sendPost(String url, String postParams) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Host", HOST_URL);
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Referer", REFERER_URL);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

		connection.setDoOutput(true);
		connection.setDoInput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private String _getPageContent(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : cookies) {
			connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		cookies.addAll(connection.getHeaderFields().get("Set-Cookie"));
		return response.toString();
	}

}
