package org.openmrs.module.sync2.api.validator.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.api.validator.SyncConfigurationValidator;
import org.springframework.stereotype.Component;

@Component
public class SyncConfigurationInstanceIdValidator implements SyncConfigurationValidator {

    private static final String ERROR_CODE = "sync2.error.instanceIdIsNotSet";

    @Override
    public void validate(SyncConfiguration syncConfiguration, Errors errors) {
        if (StringUtils.isBlank(syncConfiguration.getGeneral().getLocalInstanceId())) {
            errors.addErrorCode(ERROR_CODE);
        }
    }
}
