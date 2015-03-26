package org.betarss.provider;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;
import org.betarss.exception.FeedFilterException;
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryResults;
import org.springframework.stereotype.Service;

@Service
public class TorrentFilterer {

	private static final Pattern expressionPattern = Pattern.compile("[\\!\\d\\w_\\- ]+", Pattern.CASE_INSENSITIVE);

	@SuppressWarnings("unchecked")
	public List<Torrent> filter(List<Torrent> torrents, String filter) throws FeedFilterException {
		List<Torrent> result = null;

		if (filter == null || filter.isEmpty()) {
			return torrents;
		}

		try {
			Query q = new Query();
			q.parse("SELECT * FROM org.betarss.domain.Torrent WHERE " + getAsSql(filter));
			QueryResults qr = q.execute(torrents);
			result = qr.getResults();

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
