package org.openmrs.module.sync2.api.mapper.impl;

import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.sync2.api.mapper.EventConfigurationMapper;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.springframework.stereotype.Component;

@Component("sync2.EventConfigurationMapper")
public class EventConfigurationMapperImpl implements EventConfigurationMapper<FeedConfiguration> {

	@Override
	public EventConfiguration map(FeedConfiguration feedConfiguration) {
		return new EventConfiguration(feedConfiguration.getLinkTemplates());
	}
}
