package org.betarss.domain;

public enum Provider {
	CPASBIEN("cpasbien", Language.FR, Language.VOSTFR), //
	EZTV("eztv", Language.EN), //
	KICKASS("kickass", Language.EN), //
	SHOWRSS("showrss", Language.EN);

	String id;
	Language[] languages;

	Provider(String id, Language... languages) {
		this.id = id;
		this.languages = languages;
	}

	public boolean supportLanguage(Language language) {
		if (language != null) {
			for (Language l : languages) {
				if (language == l) {
					return true;
				}
			}
		}
		return false;
	}

	public Language[] getSupportedLanguages() {
		return languages;
	}

	public static Provider parse(String providerId) {
		for (Provider p : values()) {
			if (p.id.equals(providerId)) {
				return p;
			}
		}
		throw new IllegalArgumentException("provider " + providerId + " is not supported (cpasbien|eztv|kickass)");
	}

}
