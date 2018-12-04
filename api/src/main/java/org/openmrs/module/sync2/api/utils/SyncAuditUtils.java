package org.openmrs.module.sync2.api.utils;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.sql.Timestamp;

import static org.openmrs.module.sync2.api.utils.SyncUtils.getLocalBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getLocalInstanceId;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getParentBaseUrl;

public class SyncAuditUtils {

    public static AuditMessage prepareBaseAuditMessage(String operation, String clientName) {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditMessage.setOperation(operation);
        auditMessage.setParentUrl(getParentBaseUrl(clientName));
        auditMessage.setLocalUrl(getLocalBaseUrl());
        auditMessage.setCreatorInstanceId(getLocalInstanceId());

        return auditMessage;
    }
}
