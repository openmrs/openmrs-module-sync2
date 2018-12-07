package org.openmrs.module.sync2.api.mapper;

import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;

public interface EventConfigurationMapper<T> {

	EventConfiguration map(T externalConfiguration);
}
