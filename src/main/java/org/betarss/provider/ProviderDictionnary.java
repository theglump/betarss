package org.betarss.provider;

import java.util.List;

import org.apache.camel.spi.Language;
import org.betarss.domain.Quality;

public interface ProviderDictionnary {

	List<String> getLanguageFilter(Language language);

	List<String> getQualityFilter(Quality quality);

	// PROPER and stuff
	List<String> getTypeFilter(Quality quality);
}
