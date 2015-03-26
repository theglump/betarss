package org.betarss.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.betarss.domain.ShowEpisode;
import org.betarss.domain.Torrent;

public class Shows {

	private final static Pattern SHOW_EPISODE_PATTERN = Pattern.compile("(.*) S0?(\\d+)E0?(\\d+)");
	private final static Pattern SHOW_EPISODE_PATTERN_OLD_SCHOOL = Pattern.compile("(.*) (\\d+)x(\\d+)");

	public static boolean isDoubleEpisode(String label) {
		return Pattern.matches(".*S[0-9]{2,2}E[0-9]{2,2}\\-[0-9]{2,2}.*", label);
	}

	public static String formatEpisodeNumber(Integer season, Integer episode) {
		return formatSeason(season) + "E" + (episode < 10 ? "0" + episode : episode);
	}

	public static String formatSeason(Integer season) {
		return "S" + (season < 10 ? "0" + season : season);
	}

	public static String formatSeasonOldSchool(Integer season) {
		return season + "x";
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

	public static ShowEpisode createShowEpisode(Torrent t) {
		return createShowEpisode(t.title);
	}

	public static boolean sameShowEpisode(Torrent t1, Torrent t2) {
		return createShowEpisode(t1).equals(createShowEpisode(t2));
	}

	public static ShowEpisode createShowEpisode(String torrentTile) {
		Matcher m = SHOW_EPISODE_PATTERN.matcher(torrentTile);
		if (m.find()) {
			return createShowEpisode(m);
		}
		m = SHOW_EPISODE_PATTERN_OLD_SCHOOL.matcher(torrentTile);
		if (m.find()) {
			return createShowEpisode(m);
		}
		return new ShowEpisode();
	}

	private static ShowEpisode createShowEpisode(Matcher m) {
		ShowEpisode showEpisode = new ShowEpisode();
		showEpisode.show = m.group(1);
		showEpisode.season = Integer.parseInt(m.group(2));
		showEpisode.episode = Integer.parseInt(m.group(3));
		return showEpisode;
	}

	public static void main(String[] argz) {
		ShowEpisode createShowEpisode = createShowEpisode("Grey's Anatomy 11x16 Don't Dream It's Over 720p");
		ShowEpisode createShowEpisode1 = createShowEpisode("Grey's Anatomy S11E16");
		System.out.println(createShowEpisode.equals(createShowEpisode1));
		//		System.out.println(createShowEpisode.show);
		//		System.out.println(createShowEpisode.season);
		//		System.out.println(createShowEpisode.episode);
	}
}
