package org.betarss.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.betarss.domain.Torrent;

public class Torrents {
	public static List<Torrent> sort(List<Torrent> torrents) {
		Collections.sort(torrents, new Comparator<Torrent>() {

			@Override
			public int compare(Torrent e1, Torrent e2) {
				return e1.title.compareTo(e2.title);
			}
		});
		return torrents;
	}
}
