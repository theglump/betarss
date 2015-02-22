package org.betarss.core;

import org.betarss.domain.Feed;

public interface IRssProducer {

	public String produceRSS2(Feed feed) throws Exception;

}
