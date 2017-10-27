package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.Sync2Constants;
import org.openmrs.module.sync2.api.Sync2ConfigurationService;
import org.openmrs.module.sync2.api.model.configuration.Sync2Configuration;
import org.openmrs.module.sync2.api.utils.Sync2Utils;
import org.springframework.stereotype.Component;

@Component
public class Sync22ConfigurationServiceImpl implements Sync2ConfigurationService {

    private Sync2Configuration sync2Configuration;

    public Sync22ConfigurationServiceImpl() {
        if (Sync2Utils.resourceFileExists(Sync2Constants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION)) {
            this.sync2Configuration =
                    Sync2Utils.parseJsonFileToSyncConfiguration(Sync2Constants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
        } else {
            this.sync2Configuration =
                    Sync2Utils.parseJsonFileToSyncConfiguration(Sync2Constants.SYNC2_PATH_TO_DEFAULT_CONFIGURATION);
        }
    }

    @Override
    public void saveConfiguration(Sync2Configuration configuration) {
        Sync2Utils.writeSyncConfigurationToJsonFile(configuration, Sync2Constants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
        this.sync2Configuration = configuration;
    }

    @Override
    public void saveConfiguration(String jsonConfiguration) {
        if (Sync2Utils.isValidateJson(jsonConfiguration)) {
            Sync2Configuration customConfiguration = Sync2Utils.parseJsonStringToSyncConfiguration(jsonConfiguration);
            Sync2Utils.writeSyncConfigurationToJsonFile(customConfiguration,
                    Sync2Constants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
            this.sync2Configuration = customConfiguration;
        }
    }

    @Override
    public Sync2Configuration getSync2Configuration() {
        return this.sync2Configuration;
    }

}
