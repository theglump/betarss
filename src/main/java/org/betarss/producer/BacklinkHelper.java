package org.betarss.producer;

import java.io.IOException;

import org.betarss.exception.BetarssException;
import org.betarss.infrastructure.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BacklinkHelper {

	@Autowired
	private ConfigurationService configurationService;

	public String create(String location) {
		String applicationUrl;
		try {
			applicationUrl = configurationService.getBaseUrl();
		} catch (IOException e) {
			throw new BetarssException(e);
		}
		return applicationUrl + (!applicationUrl.endsWith("/") ? "/" : "") + "torrent?location=" + location;
	}
}
