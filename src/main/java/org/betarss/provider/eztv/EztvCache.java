package org.betarss.provider.eztv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.infrastructure.AbstractCache;
import org.betarss.infrastructure.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EztvCache extends AbstractCache<String, Integer> {

	private static final Pattern OPTION_VALUE_PATTERN = Pattern.compile("<option value=\"(\\d+)\">(((?!</option>).)*)</option>");

	@Autowired
	private HttpService httpService;

	@Override
	protected void init() {
		String html = httpService.get("http://eztv.ch", 10);
		Matcher m = OPTION_VALUE_PATTERN.matcher(html);
		while (m.find()) {
			String showName = m.group(2).toLowerCase();
			Integer id = Integer.parseInt(m.group(1));
			put(showName, id);
		}
	}

	@Override
	protected boolean lazy() {
		return true;
	}

}
