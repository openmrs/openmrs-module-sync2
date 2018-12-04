package org.openmrs.module.sync2.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.scheduler.SyncSchedulerService;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.api.validator.SyncConfigurationValidator;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.sync2.SyncConstants.CONFIGURATION_DIR;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_NAME_OF_CUSTOM_CONFIGURATION;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.ABSOLUTE;
import static org.openmrs.module.sync2.api.model.enums.ResourcePathType.RELATIVE;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.customConfigExists;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.isValidateJson;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonFileToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonFile;

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
    public ClassConfiguration getClassConfiguration(String category, SyncOperation operation) {
        ClassConfiguration result = null;
        if (StringUtils.isNotBlank(category) && operation != null) {
            SyncMethodConfiguration methodConfiguration = getSyncMethodConfiguration(operation);
            List<ClassConfiguration> classes = getClassesConfiguration(methodConfiguration);
            for (ClassConfiguration classConfiguration : classes) {
                if (classConfiguration.getCategory().equalsIgnoreCase(category)) {
                    result = classConfiguration;
                    break;
                }
            }
        }
        return result;
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

    private SyncMethodConfiguration getSyncMethodConfiguration(SyncOperation operation) {
        SyncMethodConfiguration configuration;
        switch (operation) {
            case PULL:
                configuration = getSyncConfiguration().getPull();
                break;
            case PUSH:
                configuration = getSyncConfiguration().getPush();
                break;
            default:
                configuration = null;
        }
        return configuration;
    }

    private List<ClassConfiguration> getClassesConfiguration(SyncMethodConfiguration methodConfiguration) {
        List<ClassConfiguration> result = new ArrayList<>();
        if (methodConfiguration != null) {
            result = methodConfiguration.getClasses();
        }
        return result;
    }
}
