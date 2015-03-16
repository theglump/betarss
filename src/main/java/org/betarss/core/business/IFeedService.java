package org.betarss.core.business;

import java.io.IOException;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedSearch;
import org.betarss.exception.FeedFilterException;

public interface IFeedService {

	Feed getFeed(FeedSearch parameter) throws IOException, FeedFilterException;

}