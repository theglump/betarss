package org.betarss.infrastructure;

import java.io.IOException;
import java.util.Properties;

import org.betarss.domain.Language;
import org.betarss.domain.Provider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Service
public class ConfigurationService {

	private String baseUrl;
	
	public String getApplicationUrl() {
		return "http://192.168.1.39:8080/betarss/";
	}

	public ListMultimap<Language, Provider> getBetarssProviders() {
		ListMultimap<Language, Provider> providers = ArrayListMultimap.create();
		providers.put(Language.FR, Provider.CPASBIEN);
		providers.put(Language.VOSTFR, Provider.CPASBIEN);
		providers.put(Language.EN, Provider.EZTV);
		return providers;
	}

	public ListMultimap<Language, Provider> getBetaseriesProviders() {
		ListMultimap<Language, Provider> providers = ArrayListMultimap.create();
		providers.put(Language.FR, Provider.CPASBIEN);
		providers.put(Language.VOSTFR, Provider.CPASBIEN);
		providers.put(Language.EN, Provider.SHOWRSS);
		return providers;
	}

	public boolean isMultiUser() {
		return false;
	}

	// FIX : Ca pue!!!
	public String getHttpSerializationDirectory1() {
		return "C:/Work/Workspaces/main/betarss/src/integration/ressources/data";
	}

	public String getHttpSerializationDirectory2() {
		return "C:/Work/Workspaces/main/betarss/src/connected/ressources/data";
	}
	
	public String getBaseUrl() throws IOException {
		if (baseUrl == null) {
			ClassPathResource resource = new ClassPathResource("swagger.properties");
			Properties p = new Properties();
			p.load(resource.getInputStream());
			baseUrl = p.getProperty("documentation.services.basePath");
		}
		return baseUrl;
	}

}
