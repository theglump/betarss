package org.betarss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mangofactory.swagger.EndpointComparator;
import com.mangofactory.swagger.OperationComparator;
import com.mangofactory.swagger.configuration.DocumentationConfig;

@Configuration
@Import(DocumentationConfig.class)
public class CustomDocumentationConfig {
	@Bean
	public EndpointComparator endPointComparator() {
		return new NameEndPointComparator();
	}

	@Bean
	public OperationComparator operationComparator() {
		return new NameOperationComparator();
	}
}
