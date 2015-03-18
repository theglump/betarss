package org.betarss.provider;

import java.io.IOException;

import org.betarss.domain.Feed;
import org.betarss.domain.BetarssSearch;

public interface ICrawler {
	Feed getFeed(BetarssSearch betarssSearch) throws IOException;
}
