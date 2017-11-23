package org.openmrs.module.sync2.api;

import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;

public interface SyncConfigurationService {

    void saveConfiguration(SyncConfiguration configuration);

    void saveConfiguration(String jsonConfiguration);

    SyncConfiguration getSyncConfiguration();
}
