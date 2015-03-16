package org.betarss.provider;

import java.io.IOException;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedSearch;
import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.betarss.domain.Quality;

public interface ISearchEngine {

	Feed getFeed(FeedSearch feedSearch) throws IOException;

	String getFilter(Language language, Quality quality);

	Provider getProvider();

}
