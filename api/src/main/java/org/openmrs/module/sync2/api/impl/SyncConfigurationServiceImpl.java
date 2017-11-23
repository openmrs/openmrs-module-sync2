package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.springframework.stereotype.Component;

@Component("sync2.syncConfigurationService")
public class SyncConfigurationServiceImpl implements SyncConfigurationService {

    private SyncConfiguration syncConfiguration;

    public SyncConfigurationServiceImpl() {
        if (SyncUtils.resourceFileExists(SyncConstants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION)) {
            this.syncConfiguration =
                    SyncUtils.parseJsonFileToSyncConfiguration(SyncConstants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
        } else {
            this.syncConfiguration =
                    SyncUtils.parseJsonFileToSyncConfiguration(SyncConstants.SYNC2_PATH_TO_DEFAULT_CONFIGURATION);
        }
    }

    @Override
    public void saveConfiguration(SyncConfiguration configuration) throws SyncException {
        SyncUtils.writeSyncConfigurationToJsonFile(configuration, SyncConstants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
        this.syncConfiguration = configuration;
    }

    @Override
    public void saveConfiguration(String jsonConfiguration) {
        if (SyncUtils.isValidateJson(jsonConfiguration)) {
            SyncConfiguration customConfiguration = SyncUtils.parseJsonStringToSyncConfiguration(jsonConfiguration);
            SyncUtils.writeSyncConfigurationToJsonFile(customConfiguration,
                    SyncConstants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
            this.syncConfiguration = customConfiguration;
        }
    }

    @Override
    public SyncConfiguration getSyncConfiguration() {
        return this.syncConfiguration;
    }

}
