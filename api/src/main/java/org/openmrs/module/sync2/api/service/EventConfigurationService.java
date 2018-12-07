package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

public interface EventConfigurationService {

	EventConfiguration getEventConfigurationByCategory(CategoryEnum categoryEnum);
}
