package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

import java.util.List;
import java.util.Map;

public interface SyncPullService {

    AuditMessage pullAndSaveObjectFromParent(CategoryEnum category, Map<String, String> resourceLinks,
                                           String action);

    AuditMessage pullAndSaveObjectFromParent(CategoryEnum category, Map<String, String> resourceLinks,
            String action, String clientName, String uuid);

    List<AuditMessage> pullAndSaveObjectFromParent(CategoryEnum category, String uuid);

    void pullAndSaveObjectsFromParent(CategoryEnum category);
}
