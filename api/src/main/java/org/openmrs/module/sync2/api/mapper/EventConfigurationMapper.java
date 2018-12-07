package org.openmrs.module.sync2.api.mapper;

import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;

/**
 * <h1>EventConfigurationMapper</h1>
 * Maps specific event configuration on to unified EventConfiguration used by EventConfigurationService.
 *
 * @see EventConfiguration
 * @see org.openmrs.module.sync2.api.service.EventConfigurationService
 * @see <a href="https://issues.openmrs.org/browse/SYNCT-287">SYNCT-287</a>
 * @since 1.4.0
 */
public interface EventConfigurationMapper<T> {

	/**
	 * <p>Maps event configuration of type T on to unified EventConfiguration.</p>
	 *
	 * @param externalConfiguration represents event configuration of specific event feed.
	 * @return unified event configuration used by EventConfigurationService.
	 */
	EventConfiguration map(T externalConfiguration);
}
