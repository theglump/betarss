package org.betarss.provider;

import org.betarss.domain.Language;
import org.betarss.domain.Quality;

public interface IFilterComputor {
	String getFilter(Language language, Quality quality);
}
