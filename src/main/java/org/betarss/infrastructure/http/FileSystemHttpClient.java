package org.betarss.infrastructure.http;

import static org.betarss.utils.HttpUtils.deserializeRequest;

import java.io.IOException;
import java.util.List;

import org.betarss.exception.BetarssException;
import org.betarss.infrastructure.ConfigurationService;
import org.betarss.infrastructure.http.NetHttpClient.Parameter;
import org.betarss.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSystemHttpClient implements HttpClient {

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public String get(String url, int maxTime) {
		return get(url);
	}

	@Override
	public String get(String url) {
		return deserializedRequest(url);
	}

	@Override
	public String post(String url, int maxTime, Parameter... parameters) {
		return post(url, parameters);
	}

	@Override
	public String post(String url, Parameter... parameters) {
		return deserializedRequest(url, parameters);
	}

	@Override
	public List<String> getTags(String url, String tagName) {
		try {
			return HttpUtils.deserializeTagRequest(configurationService.getHttpSerializationDirectory(), url, tagName);
		} catch (IOException e) {
			throw new BetarssException(e);
		}
	}

	@Override
	public byte[] getData(String url) {
		return dataAsString(url).getBytes();
	}

	@Override
	public String dataAsString(String url) {
		return deserializedRequest(url);
	}

	private String deserializedRequest(String url) {
		return deserializedRequest(url, (Parameter[]) null);
	}

	private String deserializedRequest(String url, Parameter... parameters) {
		try {
			return deserializeRequest(configurationService.getHttpSerializationDirectory(), url, parameters);
		} catch (IOException e) {
			throw new BetarssException(e);
		}
	}
}
