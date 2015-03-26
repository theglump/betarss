package org.betarss.domain;

import java.util.Date;

import org.betarss.utils.Shows;

// TODO P1 - Les services renvoient des List<Torrent> à la place des List<Feeditem>
public class Torrent {
	public String title;
	public String description;
	public String magnet;
	public String url;
	public String filename;
	public Date date;

	public ShowEpisode getShowEpisode() {
		return Shows.createShowEpisode(title);
	}
}
