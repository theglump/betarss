package org.betarss.domain;

import java.util.Date;

import org.betarss.utils.Shows;

// TODO : Use builder
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
