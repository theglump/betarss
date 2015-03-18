package org.betarss.utils;

public class FilterUtils {

	public static String joinFilterAnd(String... conditions) {
		StringBuilder result = new StringBuilder();
		for (String condition : conditions) {
			appendFilterAnd(result, condition);
		}
		return result.toString();
	}

	public static void appendFilterAnd(StringBuilder filter, String condition) {
		if (condition == null || condition.isEmpty()) {
			return;
		}
		if (filter.length() > 0) {
			filter.append("^");
		}
		filter.append(condition);
	}

}
