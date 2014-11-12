package org.betarss.utils;

import java.util.regex.Pattern;

public class ShowUtils {

	public static boolean hasMoreThanOneEp(String label)  {
		return Pattern.matches(".*S[0-9]{2,2}E[0-9]{2,2}\\-[0-9]{2,2}.*", label);
	}
	
	public static String getFormattedShowEpisode(int season, int episode) {
		return "S" + (season < 10 ? "0" + season : season) + "E" + (episode < 10 ? "0" + episode : episode);
	}
	
}
