package org.betarss.domain;

public class BetarssSearch extends BaseSearch {
	public ShowEpisode showEpisode = new ShowEpisode();

	public BetarssSearch(BaseSearch baseSearch) {
		super(baseSearch);
	}

	public BetarssSearch() {
		super();
	}
}
