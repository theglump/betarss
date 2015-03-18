package org.betarss.domain;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class AbstractSearch {
	public List<Language> languages = Lists.newArrayList();
	public List<Provider> providers = Lists.newArrayList();
	public List<Quality> qualities = Lists.newArrayList();
	public String filter;
	public Boolean magnet;
	public Boolean date;

	public AbstractSearch() {

	}

	public AbstractSearch(AbstractSearch search) {
		languages = search.languages;
		providers = search.providers;
		qualities = search.qualities;
		filter = search.filter;
		magnet = search.magnet;
		date = search.date;
	}
}
