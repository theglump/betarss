package org.betarss.core;

import java.io.IOException;

import org.betarss.domain.Feed;

public interface Crawler {

	Feed getFeed(String showName, int season) throws IOException;
	
}
