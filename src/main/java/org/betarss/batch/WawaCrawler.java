package org.betarss.batch;

import java.util.List;

import org.betarss.domain.Movie;

import com.google.common.collect.Lists;

public class WawaCrawler {

	public class Result {
		private List<Movie> toAdd = Lists.newArrayList();
		private List<Movie> toDelete = Lists.newArrayList();

		public List<Movie> getToAdd() {
			return toAdd;
		}

		public List<Movie> getToDelete() {
			return toDelete;
		}
	}

	public Result crawl() {
		return crawl(null);
	}

	public Result crawl(List<Movie> existingMovies) {
		return new Result();
	}

}
