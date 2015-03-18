package org.betarss.domain;

//TODO : P1 - faire une classe BetaseriesSearchCriteria et BetarssSearchCriteria qui heritent d'un BaseSearchCriteria qui peut s'initialiser avec lui même
public class BetarssSearch extends AbstractSearch {
	public String show;
	public Integer season;

	public BetarssSearch(AbstractSearch search) {
		super(search);
	}

	public BetarssSearch() {
		super();
	}
}
