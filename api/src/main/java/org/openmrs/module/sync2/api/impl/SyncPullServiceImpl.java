package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.sync.SyncPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.PULL_SUCCESS_MESSAGE;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {

    @Autowired
    SyncAuditService auditService;

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();

    public void pullDataFromParentAndSave(String category, Map<String, String> resourceLinks, String address, String action) {

        try {
            Object pulledObject = syncClient.pullDataFromParent(category, resourceLinks, address);
            syncPersistence.persistRetrievedData(pulledObject, action);
            auditService.saveSuccessfulAudit(category, address, action, PULL_SUCCESS_MESSAGE);
        } catch (Exception e) {
            auditService.saveFailedAudit(category, address, action, e.getMessage());
        }
    }
}
