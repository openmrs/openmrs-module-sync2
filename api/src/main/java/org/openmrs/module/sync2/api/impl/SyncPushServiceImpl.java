package org.openmrs.module.sync2.api.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.SyncPushService;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.sync.SyncPersistence;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("sync2.syncPushService")
public class SyncPushServiceImpl implements SyncPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushService.class);

    private static final String RESOURCE_PREFERRED_CLIENT = "sync2.resource.preferred.client";

    @Autowired
    private SyncConfigurationService configurationService;

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();

    @Override
    public void readDataAndPushToParent(String category, Map<String, String> resourceLinks, String address, String action) {
        LOGGER.info(String.format("SyncPushService category: %s, address: %s, action: %s", category, address, action));
        // TODO: stub!

        String preferredClient = Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);

        String uuid = SyncUtils.extractUUIDFromResourceLinks(resourceLinks);
        Object data = syncPersistence.retrieveData(preferredClient, category, uuid);

        syncClient.pushDataToParent(data, resourceLinks, getParentUri());
    }

    private String getParentUri() {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }
}
