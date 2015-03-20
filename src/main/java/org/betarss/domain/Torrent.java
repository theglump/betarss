package org.betarss.domain;

import java.util.Date;

// TODO P1 - Les services renvoient des List<Torrent> à la place des List<Feeditem>
public class Torrent<C extends Content> {
	public C content;
	public String title;
	public String description;
	public String magnet;
	public String url;
	public String filename;
	public Date date;
}
