package org.openmrs.module.sync2.api.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.SyncPushService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.sync.SyncPersistence;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.PUSH_ACTION;
import static org.openmrs.module.sync2.SyncConstants.PUSH_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPreferredUrl;

@Component("sync2.syncPushService")
public class SyncPushServiceImpl implements SyncPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushService.class);

    private static final String RESOURCE_PREFERRED_CLIENT = "sync2.resource.preferred.client";

    @Autowired
    private SyncConfigurationService configurationService;

    @Autowired
    private SyncAuditService auditService;

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();

    @Override
    public void readDataAndPushToParent(String category, Map<String, String> resourceLinks, String address, String action) {
        LOGGER.info(String.format("SyncPushService category: %s, address: %s, action: %s", category, address, action));

        String preferredClient = Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);
        
        AuditMessage auditMessage = prepareBaseAuditMessage();
        auditMessage.setResourceName(category);
        auditMessage.setResourceUrl(getPreferredUrl(resourceLinks));
        // TODO: set action & operation
        // TODO: set links

        try {
            String uuid = SyncUtils.extractUUIDFromResourceLinks(resourceLinks);
            Object data = syncPersistence.retrieveData(preferredClient, category, uuid);
            syncClient.pushDataToParent(data, resourceLinks, getParentUri());
    
            auditMessage.setSuccess(true);
            auditMessage.setError(PUSH_SUCCESS_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Problem with pushing to parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setError(ExceptionUtils.getFullStackTrace(e));
        } finally {
            auditService.saveAuditMessage(auditMessage);
        }
    }

    private String getParentUri() {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }
    
    private AuditMessage prepareBaseAuditMessage() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditMessage.setAction(PUSH_ACTION); // TODO: rename to PUSH_OPERATION
        return auditMessage;
    }
}
