package org.betarss.infrastructure;

import org.betarss.utils.HttpUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class SpringApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		HttpUtils.avoidHttpsErrors();
	}

}
