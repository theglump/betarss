package org.betarss.config;

import static com.mangofactory.swagger.models.IgnorableTypeRule.ignorable;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.configuration.ExtensibilityModule;
import com.mangofactory.swagger.models.TypeProcessingRule;

public class ExampleExtensibilityModule extends ExtensibilityModule {

	private final ObjectMapper documentationObjectMapper;

	@Autowired
	public ExampleExtensibilityModule(ObjectMapper documentationObjectMapper) {
		this.documentationObjectMapper = documentationObjectMapper;
		this.documentationObjectMapper.addMixInAnnotations(HttpHeaders.class, HttpHeadersMixin.class);
	}

	@Override
	protected void customizeTypeProcessingRules(List<TypeProcessingRule> rules) {
		rules.add(ignorable(UriComponentsBuilder.class));
		// rules.add(new AlternateTypeProcessingRule(BigDecimal.class, Double.class));
		// rules.add(new AlternateTypeProcessingRule(LocalDate.class, Date.class));
		//rules.add(hashmapAlternate(String.class, Pet.class));
	}

	private class HttpHeadersMixin {
		@JsonIgnore
		public void setIfNoneMatch(List<String> ifNoneMatchList) {
		}

		@JsonIgnore
		public void setLocation(URI uri) {
		}
	}

}