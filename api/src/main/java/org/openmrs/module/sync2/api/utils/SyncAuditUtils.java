package org.openmrs.module.sync2.api.utils;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.sql.Timestamp;

import static org.openmrs.module.sync2.api.utils.SyncUtils.getLocalBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getParentBaseUrl;

public class SyncAuditUtils {

    public static AuditMessage prepareBaseAuditMessage(String operation) {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditMessage.setOperation(operation);
        auditMessage.setParentUrl(getParentBaseUrl());
        auditMessage.setLocalUrl(getLocalBaseUrl());

        return auditMessage;
    }
}
