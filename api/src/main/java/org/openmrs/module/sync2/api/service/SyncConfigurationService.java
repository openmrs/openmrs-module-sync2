package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.validator.Errors;

public interface SyncConfigurationService {

    void saveConfiguration(SyncConfiguration configuration);

    void saveConfiguration(String jsonConfiguration);

    SyncConfiguration getSyncConfiguration();

    Errors validateConfiguration();
}
