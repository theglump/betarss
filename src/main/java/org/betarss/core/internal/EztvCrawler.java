package org.betarss.core.internal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.betarss.core.ICrawler;
import org.betarss.domain.Feed;
import org.betarss.domain.FeedBuilder;
import org.betarss.domain.FeedItem;
import org.betarss.domain.FeedItemBuilder;
import org.betarss.utils.ShowUtils;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class EztvCrawler implements ICrawler {

	private static final int DATE = 2;
	private static final int TITLE = 4;
	private static final int LOCATION = 7;

	private static final Map<String, Integer> TV_SHOW_IDS = Maps.newConcurrentMap();

	private static final Pattern EPISODE_ITEM_PATTERN = Pattern
			.compile(
					"(Added on: <b>(\\d+, \\w+, \\d+)</b>)|(title=\"(((?!\").)*MB\\))\"((?!forum_thread_post_end).)*<a href=\"(magnet((?!\").)*)\"((?!magnet).)*)",
					Pattern.DOTALL);

	@Override
	public Feed getFeed(String showName, int season) throws IOException {
		Integer id = getTvShowId(showName);
		List<FeedItem> feedItems = getFeed(fetchHtml(id, showName, season));
		return FeedBuilder.start().withFeedItems(feedItems).get();
	}

	private List<FeedItem> getFeed(String html) throws IOException {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		Matcher m = EPISODE_ITEM_PATTERN.matcher(html);
		Date d = null;
		while (m.find()) {
			if (m.group(DATE) != null) {
				d = parseDate(m.group(DATE));
			} else {
				String title = m.group(TITLE);
				FeedItem feedItem = FeedItemBuilder.start().withTitle(title).withDescription(title).withDate(d).withLocation(m.group(LOCATION)).get();
				feedItems.add(feedItem);
			}
		}
		return feedItems;
	}

	private Date parseDate(String date) {
		try {
			return new SimpleDateFormat("dd, MMMMM, yyyy", Locale.US).parse(date);
		} catch (java.text.ParseException e) {
			return new Date();
		}
	}

	private Integer getTvShowId(String showName) throws IOException {
		if (!TV_SHOW_IDS.containsKey(showName)) {
			String html = Jsoup.connect("http://eztv.ch").userAgent("Mozilla").get().html();
			Matcher matcher = Pattern.compile("<option value=\"(\\d+)\">" + showName + "</option>", Pattern.CASE_INSENSITIVE).matcher(html);
			if (matcher.find()) {
				TV_SHOW_IDS.put(showName, Integer.parseInt(matcher.group(1)));
			}
		}
		return TV_SHOW_IDS.get(showName);
	}

	private String fetchHtml(Integer id, String showName, int season) {
		try {
			String url = "https://eztv.ch/search/";
			URL obj = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			String postParams = "SearchString=" + id.toString() + "&SearchString1=" + ShowUtils.getFormattedShowSeason(season);

			// Acts like a browser
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Host", "eztv.ch");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Referer", "https://eztv.ch");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

			conn.setDoOutput(true);
			conn.setDoInput(true);

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
			return response.toString();
		} catch (Exception e) {
			return null;
		}
	}

	//		while (m.find()) {
	//			for (int i = 0; i < m.groupCount(); i++) {
	//				System.out.println(i + ":" + m.group(i));
	//			}
	//			System.out.print("*************************************");
	//		}
	//		return feedItems;

}
