package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.sync.SyncPersistence;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();

    public void pullDataFromParentAndSave(String category, Map<String, String> resourceLinks, String address) {

        Object pulledObject = syncClient.pullDataFromParent(category, resourceLinks, address);

        syncPersistence.persistRetrievedData(pulledObject);
    }
}
