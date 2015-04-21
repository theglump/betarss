package org.betarss.producer;

import org.betarss.infrastructure.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BacklinkHelper {

	@Autowired
	private ConfigurationService configurationService;
	
	public String create(String location) {
		String applicationUrl = configurationService.getApplicationUrl();
		return applicationUrl + (!applicationUrl.endsWith("/") ? "/" : "") + "torrent?location=" + location;
	}
}
