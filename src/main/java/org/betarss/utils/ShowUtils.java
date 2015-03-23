package org.betarss.utils;

import java.util.regex.Pattern;

public class ShowUtils {

	public static boolean isDoubleEpisode(String label) {
		return Pattern.matches(".*S[0-9]{2,2}E[0-9]{2,2}\\-[0-9]{2,2}.*", label);
	}

	public static String formatEpisodeNumber(Integer season, Integer episode) {
		return formatSeason(season) + "E" + (episode < 10 ? "0" + episode : episode);
	}

	public static String formatSeason(Integer season) {
		return "S" + (season < 10 ? "0" + season : season);
	}

	public static String formatEpisodeWithSeason(String showName, Integer season) {
		return showName + " " + formatSeason(season);
	}

	public static String formatEpisodeUpperCase(String showName, Integer season) {
		return upperCaseString(showName) + " " + formatSeason(season);
	}

	private static String upperCaseString(String str) {
		StringBuilder sb = new StringBuilder();
		for (String word : str.split(" ")) {
			sb.append(sb.length() > 0 ? " " : "");
			sb.append(word.substring(0, 1).toUpperCase() + word.substring(1));
		}
		return sb.toString();
	}
}
