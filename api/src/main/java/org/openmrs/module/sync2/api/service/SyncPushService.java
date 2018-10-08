package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.Map;

public interface SyncPushService {

    AuditMessage readAndPushObjectToParent(String category, Map<String, String> resourceLinks, String action);

    AuditMessage readAndPushObjectToParent(String category, Map<String, String> resourceLinks, String action,
                                         String clientName);

    void readAndPushObjectsToParent(String category);
}
