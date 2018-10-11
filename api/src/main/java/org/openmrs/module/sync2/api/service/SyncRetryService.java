package org.openmrs.module.sync2.api.service;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

public interface SyncRetryService {

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    AuditMessage retryMessage(AuditMessage message) throws APIException;
}
