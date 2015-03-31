package org.betarss.infrastructure.http;

import org.betarss.infrastructure.http.NetHttpClient.Parameter;

public interface HttpClient {

	String get(String url);

	String get(final String url, int maxTime);

	String post(final String url, final Parameter... parameters);

	String post(final String url, int maxTime, final Parameter... parameters);

	byte[] getData(String url);

	String dataAsString(String url);
}
