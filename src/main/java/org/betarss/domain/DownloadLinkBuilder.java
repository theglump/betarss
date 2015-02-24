package org.betarss.domain;

public class DownloadLinkBuilder {

	private DownloadLink downloadLink = new DownloadLink();

	public static DownloadLinkBuilder start() {
		return new DownloadLinkBuilder();
	}

	public DownloadLinkBuilder withHost(String host) {
		downloadLink.setHost(host);
		return this;
	}

	public DownloadLinkBuilder withUrl(String url) {
		downloadLink.setUrl(url);
		return this;
	}

	public DownloadLink get() {
		return downloadLink;
	}
}
