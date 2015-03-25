package org.betarss.domain;

public enum Mode {
	RSS("rss"), URL("url"), HTML("html");

	private String id;

	private Mode(String id) {
		this.id = id;
	}

	public static Mode parse(String id) {
		for (Mode m : values()) {
			if (m.id.equals(id)) {
				return m;
			}
		}
		throw new IllegalArgumentException("mode " + id + " is not supported (rss|link)");
	}

}
