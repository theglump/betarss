package org.betarss.domain;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class FeedSearch implements Serializable {
	public Long id;
	public String hash;
	public String show;
	public Integer season;
	public List<Language> languages = Lists.newArrayList();
	public List<Provider> providers = Lists.newArrayList();
	public List<Quality> qualities = Lists.newArrayList();
	public String filter;
	public Boolean magnet;
	public Boolean date;
}
