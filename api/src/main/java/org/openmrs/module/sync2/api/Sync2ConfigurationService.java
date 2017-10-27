package org.openmrs.module.sync2.api;

import org.openmrs.module.sync2.api.model.configuration.Sync2Configuration;

public interface Sync2ConfigurationService {

    void saveConfiguration(Sync2Configuration configuration);

    void saveConfiguration(String jsonConfiguration);

    Sync2Configuration getSync2Configuration();
}
