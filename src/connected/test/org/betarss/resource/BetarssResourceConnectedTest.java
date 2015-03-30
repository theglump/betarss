package org.betarss.resource;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class BetarssResourceConnectedTest {

	@Autowired
	private BetarssResource resource;

	@Test
	public void search_matches_with_FR_language() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, "fr", null, null, null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("FR_language"));
	}

	@Test
	public void search_matches_with_VOSTFR_language() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, "vostfr", null, null, null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("VOSTFR_language"));
	}

	@Test
	public void search_matches_with_EN_language() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "eztv", "hd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("EN_language"));
	}

	@Test
	public void search_matches_with_EN_language_and_alternative_season_format() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("sherlock", 1, null, "eztv", null, null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("EN_language_and_alternative_season_format"));
	}

	@Test
	public void search_matches_with_EN_language_and_MAGNET_and_URL_mode() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "eztv", null, null, true, "url");
		assertThat(asString(feed)).isEqualTo(expectedResult("EN_language_and_MAGNET_and_URL_mode", "url"));
	}

	private String expectedResult(String testId) throws IOException {
		return expectedResult(testId, "rss");
	}

	private String expectedResult(String testId, String mode) throws IOException {
		String ext = "url".equals(mode) ? ".txt" : ".xml";
		Resource resource = new ClassPathResource("search_results_for_" + testId + ext);
		return Files.toString(resource.getFile(), Charsets.UTF_8);
	}

	private String asString(HttpEntity<byte[]> feed) throws IOException {
		return new String(feed.getBody(), Charsets.UTF_8);
	}
}
