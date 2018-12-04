package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.validator.Errors;

public interface SyncConfigurationService {

    void saveConfiguration(SyncConfiguration configuration);

    void saveConfiguration(String jsonConfiguration);

    SyncConfiguration getSyncConfiguration();

    ClassConfiguration getClassConfiguration(String category, SyncOperation operation);

    Errors validateConfiguration();
}
