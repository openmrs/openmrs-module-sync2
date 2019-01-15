package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;

public interface EventConfigurationService {

	EventConfiguration getEventConfigurationByCategory(SyncCategory categoryEnum);
}
