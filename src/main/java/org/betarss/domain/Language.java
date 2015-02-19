package org.betarss.domain;

public enum Language {

	FR("fr", "FRENCH"), VOSTFR("vostfr", "VOSTFR"), EN("en");

	private String language;
	private String filter;

	Language(String language) {
		this.language = language;
	}

	Language(String language, String filter) {
		this(language);
		this.filter = filter;
	}

	public String getFilter() {
		return filter;
	}

	public static Language parse(String language) {
		for (Language l : values()) {
			if (l.language.equals(language)) {
				return l;
			}
		}
		throw new IllegalArgumentException("language " + language + " is not supported (fr|vostfr|en)");
	}
}
