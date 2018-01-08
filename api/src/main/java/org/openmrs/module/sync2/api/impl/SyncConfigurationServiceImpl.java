package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.scheduler.SyncSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.sync2.SyncConstants.SYNC2_PATH_TO_CUSTOM_CONFIGURATION;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.*;

@Component("sync2.syncConfigurationService")
public class SyncConfigurationServiceImpl implements SyncConfigurationService {

    private SyncConfiguration syncConfiguration;

    @Autowired
    private SyncSchedulerService schedulerService;

    public SyncConfigurationServiceImpl() {
        this.syncConfiguration = resourceFileExists(SYNC2_PATH_TO_CUSTOM_CONFIGURATION) ?
                parseJsonFileToSyncConfiguration(SYNC2_PATH_TO_CUSTOM_CONFIGURATION) :
                parseJsonFileToSyncConfiguration(SYNC2_PATH_TO_DEFAULT_CONFIGURATION);
    }

    @Override
    public void saveConfiguration(SyncConfiguration configuration) throws SyncException {
        writeSyncConfigurationToJsonFile(configuration, SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
        this.syncConfiguration = configuration;
        schedulerService.runSyncScheduler();
    }

    @Override
    public void saveConfiguration(String jsonConfiguration) {
        if (isValidateJson(jsonConfiguration)) {
            SyncConfiguration customConfiguration = parseJsonStringToSyncConfiguration(jsonConfiguration);
            writeSyncConfigurationToJsonFile(customConfiguration, SYNC2_PATH_TO_CUSTOM_CONFIGURATION);
            this.syncConfiguration = customConfiguration;

            schedulerService.runSyncScheduler();
        }
    }

    @Override
    public SyncConfiguration getSyncConfiguration() {
        return this.syncConfiguration;
    }

}
