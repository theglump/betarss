package org.betarss.provider.cpasbien;

import org.betarss.domain.Language;
import org.betarss.domain.Quality;
import org.betarss.provider.FilterComputor;
import org.betarss.utils.FilterUtils;
import org.springframework.stereotype.Service;

@Service
public class CpasbienFilterComputor implements FilterComputor {

	@Override
	public String getFilter(Language language, Quality quality) {
		StringBuilder filter = new StringBuilder();
		if (language == Language.FR) {
			FilterUtils.appendFilterAnd(filter, "FRENCH");
		} else if (language == Language.VOSTFR) {
			FilterUtils.appendFilterAnd(filter, "VOSTFR");
		}
		if (quality == Quality.SD) {
			FilterUtils.appendFilterAnd(filter, "!720p");
		} else if (quality == Quality.HD) {
			FilterUtils.appendFilterAnd(filter, "720p");
		}
		return filter.toString();
	}

}
