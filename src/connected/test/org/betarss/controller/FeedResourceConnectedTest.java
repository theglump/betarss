package org.betarss.controller;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.betarss.controller.FeedResource;
import org.betarss.infrastructure.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gentlyweb.utils.IOUtils;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context-connected.xml")
public class FeedResourceConnectedTest {

	private static final boolean PRODUCE_DATA = false;

	@Autowired
	private FeedResource resource;

	@Autowired
	private ConfigurationService configurationService;

	private String currentResult;

	@Test
	public void search_matches_with_CPASBIEN_provider() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "cpasbien", null, null, true, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("CPASBIEN_provider"));
	}

	@Test
	public void search_matches_with_EZTV_provider() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "eztv", null, null, true, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("EZTV_provider"));
	}

	@Test
	public void search_matches_with_SHOWRSS_provider() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "showrss", null, null, true, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("SHOWRSS_provider"));
	}

	@Test
	public void search_matches_with_KICKASS_provider() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "kickass", null, null, true, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("KICKASS_provider"));
	}

	private String expectedResult(String testId) throws IOException {
		return expectedResult(testId, "rss");
	}

	private String expectedResult(String testId, String mode) throws IOException {
		String ext = "url".equals(mode) ? ".txt" : "html".equals(mode) ? ".html" : ".xml";
		if (PRODUCE_DATA) {
			IOUtils.writeBytesToFile(
					new File(configurationService.getHttpSerializationDirectory2() + File.separator + "results_for_" + testId + ext),
					currentResult.getBytes());
			return currentResult;
		}
		String result = Files.toString(new File(configurationService.getHttpSerializationDirectory2() + File.separator + "results_for_" + testId
				+ ext), Charsets.UTF_8);
		return result;
	}

	private String asString(HttpEntity<byte[]> feed) throws IOException {
		if (PRODUCE_DATA) {
			currentResult = new String(feed.getBody(), Charsets.UTF_8);
		}
		return new String(feed.getBody(), Charsets.UTF_8);
	}
}
