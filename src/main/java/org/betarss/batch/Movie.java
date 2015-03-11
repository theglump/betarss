package org.betarss.batch;

import java.util.List;

public class Movie {

	private Long id;
	private String name;
	private String year;
	private String imdbUrl;
	private String allocineUrl;
	private List<DownloadLink> downloadLinks;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getImdbUrl() {
		return imdbUrl;
	}

	public void setImdbUrl(String imdbUrl) {
		this.imdbUrl = imdbUrl;
	}

	public String getAllocineUrl() {
		return allocineUrl;
	}

	public void setAllocineUrl(String allocineUrl) {
		this.allocineUrl = allocineUrl;
	}

	public List<DownloadLink> getDownloadLinks() {
		return downloadLinks;
	}

	public void setDownloadLinks(List<DownloadLink> downloadLinks) {
		this.downloadLinks = downloadLinks;
	}

}
