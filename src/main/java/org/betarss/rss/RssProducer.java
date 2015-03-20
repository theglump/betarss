package org.betarss.rss;

import org.betarss.domain.Feed;

public interface RssProducer {

	public String produceRSS2(Feed feed) throws Exception;

}
