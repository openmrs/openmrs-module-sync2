package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.List;
import java.util.Map;

public interface SyncPushService {

    AuditMessage readAndPushObjectToParent(SyncCategory category, Map<String, String> resourceLinks, String action);

    AuditMessage readAndPushObjectToParent(SyncCategory category, Map<String, String> resourceLinks, String action,
                                         String clientName, String uuid);

    List<AuditMessage> readAndPushObjectToParent(SyncCategory category, String uuid);

    void readAndPushObjectsToParent(SyncCategory category);

    AuditMessage mergeForcePush(Object merged, SyncCategory category, Map<String, String> resourceLinks,
            String action, String uuid);
}
