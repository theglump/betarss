package org.betarss.batch;

import java.io.IOException;
import java.util.List;

import org.betarss.exception.BetarssException;

import com.google.common.collect.Lists;

public class WawaCrawler {

	private final WawaHttpUrlConnection connection = new WawaHttpUrlConnection();

	public class Result {
		private List<Movie> moviesToAdd = Lists.newArrayList();
		private List<Movie> moviesToDelete = Lists.newArrayList();

		public List<Movie> getMoviesToAdd() {
			return moviesToAdd;
		}

		public List<Movie> getMoviesToDelete() {
			return moviesToDelete;
		}
	}

	public Result crawl() throws IOException {
		return crawl(null);
	}

	public Result crawl(List<Movie> existingMovies) throws IOException {
		login();
		String html = connection.getPageContent("http://wawawa.com/lzpdekode");
		return new Result();
	}

	private void login() throws IOException {
		if (!connection.login("theglump", "ctkcr9jn")) {
			throw new BetarssException("could not log in wawa website");
		}
	}

}
