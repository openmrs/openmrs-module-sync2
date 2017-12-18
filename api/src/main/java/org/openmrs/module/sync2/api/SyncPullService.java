package org.openmrs.module.sync2.api;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.Map;

public interface SyncPullService {

    AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks, String address, String action);
}
