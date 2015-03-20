package org.betarss.domain;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class BaseSearch {
	public List<Language> languages = Lists.newArrayList();
	public List<Provider> providers = Lists.newArrayList();
	public List<Quality> qualities = Lists.newArrayList();
	public String filter;
	public Boolean magnet;
	public Boolean date;

	public BaseSearch() {

	}

	public BaseSearch(BaseSearch baseSearch) {
		languages = baseSearch.languages;
		providers = baseSearch.providers;
		qualities = baseSearch.qualities;
		filter = baseSearch.filter;
		magnet = baseSearch.magnet;
		date = baseSearch.date;
	}
}
