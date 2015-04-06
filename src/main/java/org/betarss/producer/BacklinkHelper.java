package org.betarss.producer;

import org.betarss.infrastructure.ConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class BacklinkHelper {

	private ConfigurationService configurationService;
	
	public String create(String location) {
		return configurationService.getApplicationUrl() + "/torrent?location=" + location;
	}
}
