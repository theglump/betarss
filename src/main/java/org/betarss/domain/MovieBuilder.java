package org.betarss.domain;

import java.util.List;

public class MovieBuilder {

	private Movie movie = new Movie();

	public static MovieBuilder start() {
		return new MovieBuilder();
	}

	public MovieBuilder setId(Long id) {
		movie.setId(id);
		return this;
	}

	public MovieBuilder setName(String name) {
		movie.setName(name);
		return this;
	}

	public MovieBuilder setYear(String year) {
		movie.setYear(year);
		return this;
	}

	public MovieBuilder setImdbUrl(String imdbUrl) {
		movie.setImdbUrl(imdbUrl);
		return this;
	}

	public MovieBuilder setAllocineUrl(String allocineUrl) {
		movie.setAllocineUrl(allocineUrl);
		return this;
	}

	public MovieBuilder setDownloadLinks(List<DownloadLink> downloadLinks) {
		movie.setDownloadLinks(downloadLinks);
		return this;
	}

	public Movie get() {
		return movie;
	}

}
