package org.betarss.core;

import org.betarss.core.internal.CpasbienCrawler;
import org.betarss.core.internal.EztvCrawler;
import org.betarss.domain.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrawlerProvider {

	@Autowired
	private CpasbienCrawler cpasbienCrawler;

	@Autowired
	private EztvCrawler eztvCrawler;

	public ICrawler provide(Language language) {
		return language == Language.EN ? eztvCrawler : cpasbienCrawler;
	}
}
