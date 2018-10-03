package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.scheduler.SyncSchedulerService;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.api.validator.SyncConfigurationValidator;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static org.openmrs.module.sync2.SyncConstants.CONFIGURATION_DIR;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_NAME_OF_CUSTOM_CONFIGURATION;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.ABSOLUTE;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.RELATIVE;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.customConfigExists;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonFileToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonFile;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.isValidateJson;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;

@Component("sync2.syncConfigurationService")
public class SyncConfigurationServiceImpl implements SyncConfigurationService {

    private SyncConfiguration syncConfiguration;

    @Autowired
    private SyncSchedulerService schedulerService;

    public SyncConfigurationServiceImpl() {
        String configFilePath = getConfigFilePath();

        this.syncConfiguration = customConfigExists(configFilePath) ?
                parseJsonFileToSyncConfiguration(configFilePath, ABSOLUTE) :
                parseJsonFileToSyncConfiguration(SYNC2_PATH_TO_DEFAULT_CONFIGURATION, RELATIVE);
    }

    @Override
    public void saveConfiguration(SyncConfiguration configuration) throws SyncException {
        writeSyncConfigurationToJsonFile(configuration, getConfigFilePath());
        this.syncConfiguration = configuration;
        schedulerService.runSyncScheduler();
    }

    @Override
    public void saveConfiguration(String jsonConfiguration) {
        if (isValidateJson(jsonConfiguration)) {
            SyncConfiguration customConfiguration = parseJsonStringToSyncConfiguration(jsonConfiguration);
            writeSyncConfigurationToJsonFile(customConfiguration, getConfigFilePath());
            this.syncConfiguration = customConfiguration;

            schedulerService.runSyncScheduler();
        }
    }

    @Override
    public SyncConfiguration getSyncConfiguration() {
        return this.syncConfiguration;
    }

    @Override
    public Errors validateConfiguration() {
        Errors errors = new Errors();
        List<SyncConfigurationValidator> validators = Context.getRegisteredComponents(SyncConfigurationValidator.class);
        for (SyncConfigurationValidator syncConfigurationValidator : validators) {
            syncConfigurationValidator.validate(syncConfiguration, errors);
        }
        return errors;
    }

    private String getConfigFilePath() {
        File configFileFolder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(CONFIGURATION_DIR);
        return new File(configFileFolder, SYNC2_NAME_OF_CUSTOM_CONFIGURATION).getAbsolutePath();
    }
}
