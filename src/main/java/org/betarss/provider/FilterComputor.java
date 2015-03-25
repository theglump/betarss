package org.betarss.provider;

import org.betarss.domain.Language;
import org.betarss.domain.Quality;

// TODO : P1 - Faire plutot un ProviderDictionnary que le Filterer possède (en plus d'une BaseSearch)
// TODO : P1 - Le crawler pourrait ne recevoir qu'un ShowEpisode et ne pas se soucier de la qualité/langue...
public interface FilterComputor {
	String getFilter(Language language, Quality quality);
}
