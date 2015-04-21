package org.betarss.domain;

import static org.betarss.utils.Strings.append;
import static org.betarss.utils.Strings.readable;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class BaseSearch {
	public List<Language> languages = Lists.newArrayList();
	public List<Provider> providers = Lists.newArrayList();
	public List<Quality> qualities = Lists.newArrayList();
	public String filter;
	public Boolean magnet;
	public Boolean date;
	public Boolean backlink;

	public BaseSearch() {

	}

	public BaseSearch(BaseSearch baseSearch) {
		languages = baseSearch.languages;
		providers = baseSearch.providers;
		qualities = baseSearch.qualities;
		filter = baseSearch.filter;
		magnet = baseSearch.magnet;
		date = baseSearch.date;
		backlink = baseSearch.backlink;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		append(builder, readable("language", languages));
		append(builder, readable("provider", providers));
		append(builder, readable("quality", qualities));
		append(builder, readable("filter", filter));
		append(builder, readable("magnet", magnet));
		append(builder, readable("backlink", backlink));
		return builder.toString();
	}

}
