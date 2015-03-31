package org.betarss.domain;

// TODO : Use a builder
public class BetaseriesSearch extends BaseSearch {
	public String login;

	public BetaseriesSearch() {
		super();
	}

	public BetaseriesSearch(BaseSearch baseSearch) {
		super(baseSearch);
	}

	@Override
	public String toString() {
		return "login = " + login + ", " + super.toString();
	}

}
