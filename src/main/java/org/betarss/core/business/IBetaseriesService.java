package org.betarss.core.business;

import java.io.IOException;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedSearch;
import org.betarss.provider.ISearchEngine;

public interface IBetaseriesService {

	Feed getFeed(ISearchEngine searchEngine, FeedSearch feedSearch, String login) throws IOException;

}