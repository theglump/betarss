package org.betarss.domain;

public class ShowEpisode implements Content {
	public String show;
	public Integer season;
	public Integer episode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((episode == null) ? 0 : episode.hashCode());
		result = prime * result + ((season == null) ? 0 : season.hashCode());
		result = prime * result + ((show == null) ? 0 : show.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShowEpisode other = (ShowEpisode) obj;
		if (episode == null) {
			if (other.episode != null)
				return false;
		} else if (!episode.equals(other.episode))
			return false;
		if (season == null) {
			if (other.season != null)
				return false;
		} else if (!season.equals(other.season))
			return false;
		if (show == null) {
			if (other.show != null)
				return false;
		} else if (!show.equalsIgnoreCase((other.show)))
			return false;
		return true;
	}
}
