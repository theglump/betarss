package org.betarss.domain;

// TOTO : Use a builder
public class BetarssSearch extends BaseSearch {
	public ShowEpisode showEpisode = new ShowEpisode();

	public BetarssSearch(BaseSearch baseSearch) {
		super(baseSearch);
	}

	public BetarssSearch() {
		super();
	}

	@Override
	public String toString() {
		return showEpisode + ", " + super.toString();
	}

}
