package org.betarss.infrastructure;

import java.util.List;

import org.betarss.infrastructure.HttpServiceImpl.Parameter;

public interface HttpService {

	String get(String url);

	String get(final String url, int maxTime);

	String post(final String url, final Parameter... parameters);

	String post(final String url, int maxTime, final Parameter... parameters);

	List<String> getTags(String url, String tagName);

	byte[] getData(String url);

	String dataAsString(String url);
}
