package org.betarss.provider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.Feed;
import org.betarss.domain.FeedItem;
import org.betarss.domain.builder.FeedBuilder;
import org.betarss.exception.FeedFilterException;
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryResults;
import org.springframework.stereotype.Service;

@Service
public class FeedFilterer {

	private static final Pattern expressionPattern = Pattern.compile("[\\!\\d\\w_\\- ]+", Pattern.CASE_INSENSITIVE);

	@SuppressWarnings("unchecked")
	public Feed filter(Feed feed, String filter) throws FeedFilterException {
		Feed result = null;

		if (filter == null || filter.isEmpty()) {
			return feed;
		}

		try {
			Query q = new Query();
			q.parse("SELECT * FROM org.betarss.domain.FeedItem WHERE " + getAsSql(filter));
			QueryResults qr = q.execute(feed.getFeedItems());
			List<FeedItem> res = qr.getResults();
			result = FeedBuilder.start().withTitle(feed.getTitle()).withFeedItems(res).get();

		} catch (org.josql.QueryParseException e) {
			throw new FeedFilterException(e);

		} catch (QueryExecutionException e) {
			throw new FeedFilterException(e);
		}
		return result;
	}

	private String getAsSql(String filter) {
		StringBuffer expression = new StringBuffer();
		Matcher matcher = expressionPattern.matcher(filter);
		while (matcher.find()) {
			String expr = matcher.group(0);
			if (expr.startsWith("!")) {
				matcher.appendReplacement(expression, " title not LIKE '%" + expr.substring(1) + "%' ");
			} else {
				matcher.appendReplacement(expression, " title LIKE '%" + expr + "%' ");
			}
		}
		matcher.appendTail(expression);
		return expression.toString().replaceAll("\\|\\|", " OR ").replaceAll("\\^", " AND ");
	}

}