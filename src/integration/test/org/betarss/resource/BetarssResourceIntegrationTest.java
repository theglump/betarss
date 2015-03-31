package org.betarss.resource;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

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
@ContextConfiguration(locations = "classpath:application-context.xml")
public class BetarssResourceIntegrationTest {

	private static final boolean PRODUCE_DATA = true;

	@Autowired
	private BetarssResource resource;

	@Autowired
	private ConfigurationService configurationService;

	private String currentResult;

	@Test
	public void search_BETARSS_provider_EZTV_quality_SD_links_as_TORRENT() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "eztv", "sd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_EZTV_quality_SD_links_as_TORRENT", "rss"));
	}

	@Test
	public void search_BETARSS_provider_EZTV_quality_HD_links_as_MAGNET() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "eztv", "hd", null, true, "html");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_EZTV_quality_HD_links_as_MAGNET", "html"));
	}

	@Test
	public void search_BETARSS_provider_EZTV_quality_SD_links_as_MAGNET_season_format_as_SxE() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("sherlock", 1, null, "eztv", "sd", null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_EZTV_quality_SD_links_as_MAGNET_season_format_as_SxE", "rss"));
	}

	@Test
	public void search_BETARSS_language_VOSTFR_quality_SD() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, "vostfr", null, "sd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_language_VOSTFR_quality_SD", "rss"));
	}

	@Test
	public void search_BETARSS_language_FR_quality_SD() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, "fr", null, "sd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_language_FR_quality_SD", "rss"));
	}

	@Test
	public void search_BETARSS_provider_SHOWRSS_quality_SD() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "showrss", "sd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_SHOWRSS_quality_SD", "rss"));
	}

	@Test
	public void search_BETARSS_provider_SHOWRSS_quality_HD() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "showrss", "hd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_SHOWRSS_quality_HD", "rss"));
	}

	@Test
	public void search_BETARSS_provider_KICKASS_quality_SD() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "kickass", "sd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_KICKASS_quality_SD", "rss"));
	}

	@Test
	public void search_BETARSS_provider_KICKASS_quality_HD() throws Exception {
		HttpEntity<byte[]> feed = resource.specificShow("game of thrones", 4, null, "kickass", "hd", null, false, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETARSS_provider_KICKASS_quality_HD", "rss"));
	}

	@Test
	public void search_BETASERIES_language_EN_quality_SD() throws Exception {
		HttpEntity<byte[]> feed = resource.fromBetaseries("theglump", "en", null, "sd", null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_BETASERIES_language_EN_quality_SD", "rss"));
	}

	@Test
	public void search_LAST_provider_EZTV() throws Exception {
		HttpEntity<byte[]> feed = resource.latestEpisodes(null, "eztv", null, null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_LAST_provider_EZTV", "html"));
	}

	@Test
	public void search_LAST_provider_CPASBIEN() throws Exception {
		HttpEntity<byte[]> feed = resource.latestEpisodes(null, "cpasbien", null, null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_LAST_provider_CPASBIEN", "html"));
	}

	@Test
	public void search_LAST_provider_KICKASS() throws Exception {
		HttpEntity<byte[]> feed = resource.latestEpisodes(null, "kickass", null, null, true, "rss");
		assertThat(asString(feed)).isEqualTo(expectedResult("search_LAST_provider_KICKASS", "html"));
	}

	String file = null;

	private String expectedResult(String testId, String mode) throws IOException {
		String ext = "url".equals(mode) ? ".txt" : "html".equals(mode) ? ".html" : ".xml";
		if (PRODUCE_DATA) {
			IOUtils.writeBytesToFile(new File(configurationService.getHttpSerializationDirectory() + File.separator + "search_results_for_" + testId
					+ ext), currentResult.getBytes());
			return currentResult;
		}
		String result = Files.toString(new File(configurationService.getHttpSerializationDirectory() + File.separator + "search_results_for_"
				+ testId + ext), Charsets.UTF_8);
		return result;
	}

	private String asString(HttpEntity<byte[]> feed) throws IOException {
		if (PRODUCE_DATA) {
			currentResult = new String(feed.getBody(), Charsets.UTF_8);
		}
		return new String(feed.getBody(), Charsets.UTF_8);
	}
}
