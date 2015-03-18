package org.betarss.provider;

import org.betarss.domain.Language;
import org.betarss.domain.Quality;
import org.springframework.stereotype.Service;

@Service
public class DefaultFilterComputor implements IFilterComputor {

	@Override
	public String getFilter(Language language, Quality quality) {
		StringBuilder filter = new StringBuilder();
		if (quality == Quality.SD) {
			filter.append("");
		} else if (quality == Quality.HD) {
			filter.append("");
		}
		return filter.toString();

	}

}
