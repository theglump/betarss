package org.betarss.resource;

import java.io.IOException;

import org.fest.assertions.Assertions;
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
public class BetarssRessourceConnectedTest {

	@Autowired
	private BetarssResource resource;

	@Test
	public void search_matches_with_VF_language() throws Exception {
		HttpEntity<byte[]> feed = resource.feed("game of thrones", 4, "fr", null, null, null, true, true);
		Assertions.assertThat(asString(feed)).isEqualTo(expected("VF_language"));
	}

	private String expected(String testId) throws IOException {
		Resource resource = new ClassPathResource("search_results_for_" + testId + ".xml");
		return Files.toString(resource.getFile(), Charsets.UTF_8);
	}

	private String asString(HttpEntity<byte[]> feed) throws IOException {
		return new String(feed.getBody(), Charsets.UTF_8);
	}
}
