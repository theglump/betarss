package org.betarss.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.infrastructure.AbstractCache;
import org.betarss.utils.Utils;
import org.betarss.utils.Utils.Function;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class EztvCache extends AbstractCache<String, Integer> {

	private static final Pattern OPTION_VALUE_PATTERN = Pattern.compile("<option value=\"(\\d+)\">(((?!</option>).)*)</option>");

	@Override
	protected void init() {
		String html = Utils.doTry(10, new Function<String>() {

			@Override
			public String doCall() throws Exception {
				return Jsoup.connect("http://eztv.ch").userAgent("Mozilla").get().html();
			}

		});

		Matcher m = OPTION_VALUE_PATTERN.matcher(html);
		while (m.find()) {
			String showName = m.group(2).toLowerCase();
			Integer id = Integer.parseInt(m.group(1));
			put(showName, id);
		}
	}
}
