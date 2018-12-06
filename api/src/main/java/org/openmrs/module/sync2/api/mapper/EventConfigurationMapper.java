package org.openmrs.module.sync2.api.mapper;

import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;

public interface EventConfigurationMapper {

	EventConfiguration map(FeedConfiguration feedConfiguration);
}
