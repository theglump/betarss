package org.betarss.core;

import java.io.IOException;

import org.betarss.domain.Feed;

public interface ICrawler {

	Feed getFeed(String showName, int season) throws IOException;

	Feed getFeed() throws IOException;

}
