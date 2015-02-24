import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.batch.WawaCrawler;
import org.betarss.batch.WawaCrawler.Result;
import org.betarss.batch.WawaHttpUrlConnection;
import org.betarss.domain.Movie;
import org.betarss.exception.BetarssException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

public class Test {

	private List<String> cookies; // = Arrays.asList("PHPSESSID", "punbb_cookie");
	private HttpURLConnection conn;

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		Result result = new WawaCrawler().crawl();
		System.out.println("toAdd : ");
		for (Movie movie : result.getToAdd()) {

		}
		//		Pattern p3 = Pattern
		//				.compile(
		//						".*<a href=\"(http://www.imdb.com/title/(((?!\").)*))\".*<a href=\"(http://www.allocine.fr/recherche/(((?!\").)*))\" target=\"_blank\">Allociné</a>.*<em>([\\d\\.]+/10)</em>.*<span style=\"color: purple\">(\\d+p)(((?!<span style=\"color: purple\">).)*)",
		//						Pattern.DOTALL);
		//
		//		String hh = Files.toString(new File("C:/Work/Workspaces/main/files/wawa-file.txt"), Charsets.UTF_8);
		//
		//		System.out.println(hh);
		//
		//		Matcher m4 = p3.matcher(hh);
		//		while (m4.find()) {
		//			for (int i = 0; i < m4.groupCount(); i++) {
		//				System.out.println(m4.group(i));
		//			}
		//		}

		//		System.exit(0);

		String url = "http://forum.wawa-mania.ec/login.php?action=in";
		String gmail = "http://forum.wawa-mania.ec/login.php";

		//		Test http = new Test();

		WawaHttpUrlConnection wawa = new WawaHttpUrlConnection();

		// make sure cookies is turn on
		//CookieHandler.setDefault(new CookieManager());

		// 1. Send a "GET" request, so that you can extract the form's data.
		//		String page = http.GetPageContent(url);
		//		String postParams = http.getFormParams(page, "username@gmail.com", "password");

		// 2. Construct above post's content and then send a POST request for
		// authentication
		//http.GetPageContent(gmail);
		//http.sendPost(url, "req_username=theglump&req_password=ctkcr9jn&form_sent=1&redirect_url=index.php");

		boolean logged = wawa.login("theglump", "ctkcr9jn");
		if (!logged) {
			throw new BetarssException("pas pu se logger");
		}

		// 3. success then go to gmail.
		String result1 = wawa.getPageContent(gmail);
		System.out.println(result1);

		//		<span class="image_kill_referrer"></span> 300 (2006) - <span style="color: green">(7,8/10)</span> - <span style="color: purple">
		//		TiNY 720p/1080p</span> - <span style="color: orange">850/1500 MB </span></a></strong><br /></p><hr /><p><strong>
		//		<a href="http://forum.wawa-mania.ec/viewtopic.php?id=1443970" target="_blank">

		Pattern p = Pattern
				.compile("<span class=\"image_kill_referrer\"></span> (((?!<span).)*) - (((?!image).)*)<a href=\"(http://forum(((?!\").)*))\"",
						Pattern.DOTALL);

		String flux = wawa.getPageContent("http://forum.wawa-mania.ec/viewtopic.php?id=1058140");

		Matcher m = p.matcher(flux);
		while (m.find()) {
			String movie = m.group(1);
			String u = m.group(5);
			//System.out.println(movie + " - " + u);

			String h = wawa.getPageContent(u);

			List<String> matches;
			matches = match("http://www.imdb.com/title/(((?!\").)*)", h);
			//System.out.println(matches.get(0));
			matches = match("http://www.allocine.fr/recherche/(((?!\").)*)", h);
			//System.out.println(matches.get(0));
			matches = match("[\\d\\.]+/10", h);
			//System.out.println(matches.get(0));
			matches = match("<span style=\"color: purple\">(\\d+p)(((?!<span style=\"color: purple\">).)*)", h);

			System.out.println(matches.get(1));

			matches = match("(Streaming </span><a )?href=\"(((?!\").)*)\" target=\"_blank\">(((?!<).)*)((?!Mot de passe).)*", matches.get(2));
			System.out.println(matches.get(1));
			System.out.println(matches.get(2));
			System.out.println(matches.get(3));
			System.out.println(matches.get(4));

			//			Pattern p1 = Pattern
			//					.compile(
			//							".*<a href=\"(http://www.imdb.com/title/(((?!\").)*))\"(((?!allocine).)*)<a href=\"(http://www.allocine.fr/recherche/(((?!\").)*))\"(((?!em).)*)<em>([\\d\\.]+/10)</em>.*<span style=\"color: purple\">(\\d+p)(((?!<span style=\"color: purple\">).)*)",
			//							Pattern.DOTALL);
			//
			//
			//			System.out.println(h);
			//
			//			Matcher m2 = p1.matcher(h);
			//			while (m2.find()) {
			//				for (int i = 0; i < m2.groupCount(); i++) {
			//					System.out.println(m2.group(i));
			//				}
			//			}

			break;

		}

	}

	private static List<String> match(String regexp, String text) {
		List<String> r = Lists.newArrayList();
		Matcher m = Pattern.compile(regexp, Pattern.DOTALL).matcher(text);
		while (m.find()) {
			for (int i = 0; i < m.groupCount(); i++) {
				// System.out.println(i + " : " + m.group(i));
				r.add(m.group(i));
			}

		}
		return r;

	}

	private void sendPost(String url, String postParams) throws Exception {

		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();

		// Acts like a browser
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Host", "accounts.google.com");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

		conn.setDoOutput(true);
		conn.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//System.out.println(response.toString());

	}

	private String GetPageContent(String url) throws Exception {

		URL obj = new URL(url);
		conn = (HttpURLConnection) obj.openConnection();

		// default is GET
		conn.setRequestMethod("GET");

		conn.setUseCaches(false);

		// act like a browser
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		return response.toString();

	}

	public String getFormParams(String html, String username, String password) throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");

		Document doc = Jsoup.parse(html);

		// Google form id
		Element loginform = doc.getElementById("gaia_loginform");
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("Email"))
				value = username;
			else if (key.equals("Passwd"))
				value = password;
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}

		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}
		return result.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

}
