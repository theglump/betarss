package org.betarss.utils;

import java.util.List;

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

	public static void append(StringBuilder builder, String str) {
		String delim = builder.length() > 0 ? ", " : "";
		if (Strings.isNotEmpty(str)) {
			builder.append(delim).append(str);
		}
	}

	public static String readable(String param, List<?> values) {
		if (values.size() > 0) {
			return readable(param, values.get(0).toString());
		}
		return "";
	}

	public static String readable(String param, String value) {
		if (Strings.isNotEmpty(value)) {
			return param + " = " + value;
		}
		return "";
	}

	public static String readable(String param, boolean value) {
		return param + " = " + value;
	}

}
