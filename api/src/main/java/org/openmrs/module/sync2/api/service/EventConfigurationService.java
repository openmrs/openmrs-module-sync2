package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;

import java.util.Map;

public interface EventConfigurationService {

	EventConfiguration getEventConfigurationByCategory(SyncCategory categoryEnum);

	String extractUuidFromResourceLinks(Map<String, String> eventResourceLinks, String eventCategory, String preferredClient);
}
