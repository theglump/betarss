package org.betarss.domain;

public enum Quality {
	SD("sd"), HD("hd");

	String id;

	Quality(String id) {
		this.id = id;
	}

	public static Quality parse(String id) {
		for (Quality p : values()) {
			if (p.id.equals(id)) {
				return p;
			}
		}
		throw new IllegalArgumentException("quality " + id + " is not supported (hd|720p)");
	}

}
