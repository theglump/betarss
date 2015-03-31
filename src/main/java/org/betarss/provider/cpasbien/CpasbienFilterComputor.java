package org.betarss.provider.cpasbien;

import org.betarss.domain.Language;
import org.betarss.domain.Quality;
import org.betarss.provider.FilterComputor;
import org.betarss.utils.Filters;
import org.springframework.stereotype.Service;

@Service
public class CpasbienFilterComputor implements FilterComputor {

	@Override
	public String getFilter(Language language, Quality quality) {
		StringBuilder filter = new StringBuilder();
		if (language == Language.FR) {
			Filters.appendFilterAnd(filter, "FRENCH");
		} else if (language == Language.VOSTFR) {
			Filters.appendFilterAnd(filter, "VOSTFR");
		}
		if (quality == Quality.HD) {
			Filters.appendFilterAnd(filter, "720p");
		} else if (quality == Quality.SD) {
			Filters.appendFilterAnd(filter, "!720p");
		}
		return filter.toString();
	}

}
