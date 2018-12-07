package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

import java.util.List;
import java.util.Map;

public interface SyncPushService {

    AuditMessage readAndPushObjectToParent(CategoryEnum category, Map<String, String> resourceLinks, String action);

    AuditMessage readAndPushObjectToParent(CategoryEnum category, Map<String, String> resourceLinks, String action,
                                         String clientName, String uuid);

    List<AuditMessage> readAndPushObjectToParent(CategoryEnum category, String uuid);

    void readAndPushObjectsToParent(CategoryEnum category);

    AuditMessage mergeForcePush(Object merged, CategoryEnum category, Map<String, String> resourceLinks,
            String action, String uuid);
}
