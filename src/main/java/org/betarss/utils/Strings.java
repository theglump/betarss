package org.betarss.utils;

public class Strings {

	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static String defaultString(String str) {
		return defaultString(str, "");
	}

	public static String defaultString(String str, String defaultValue) {
		return isEmpty(str) ? defaultValue : str;
	}

}
