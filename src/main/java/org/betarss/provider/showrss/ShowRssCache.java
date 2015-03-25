package org.betarss.provider.showrss;

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.exception.BetarssException;
import org.betarss.infrastructure.AbstractCache;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class ShowRssCache extends AbstractCache<String, Integer> {

	private static final Pattern OPTIONS_HTML_PATTERN = compile("\"Pick a show...\"><option></option>(((?!Pick a show).)*)", Pattern.DOTALL
			| Pattern.CASE_INSENSITIVE);
	private static final Pattern OPTION_HTML_PATTERN = compile("<option value=\"(\\d+)\">(((?!</option>).)*)</option>", Pattern.DOTALL
			| Pattern.CASE_INSENSITIVE);

	@Override
	protected void init() {
		String html;
		try {
			html = Jsoup.connect("http://showrss.info/?cs=feeds").userAgent("Mozilla/5.0").get().html();
		} catch (IOException e) {
			throw new BetarssException("Couldn't create showrss cache", e);
		}

		Matcher m = OPTIONS_HTML_PATTERN.matcher(html);
		m.find();
		String optionHtml = m.group(1);

		Matcher m2 = OPTION_HTML_PATTERN.matcher(optionHtml);
		while (m2.find()) {
			String showName = m2.group(2).toUpperCase();
			Integer id = parseInt(m2.group(1));
			put(showName, id);
		}
	}

	@Override
	public Integer get(String key) {
		return super.get(key.toUpperCase());
	}

	@Override
	protected boolean lazy() {
		return true;
	}

}
