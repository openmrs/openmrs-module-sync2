package org.openmrs.module.sync2.api.validator;

import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;

public interface SyncConfigurationValidator {

    void validate(SyncConfiguration syncConfiguration, Errors errors);
}
