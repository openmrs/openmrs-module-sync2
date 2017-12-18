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

import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;
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
    public AuditMessage readDataAndPushToParent(String category, Map<String, String> resourceLinks, String address, String action) {
        LOGGER.info(String.format("SyncPushService category: %s, address: %s, action: %s", category, address, action));
        
        AuditMessage auditMessage = prepareBaseAuditMessage();
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(getPreferredUrl(resourceLinks));
        auditMessage.setAvailableResourceUrls(SyncUtils.serializeMap(resourceLinks));
        auditMessage.setParentUrl(getParentUri());
        auditMessage.setLocalUrl(getLocalUri());
        auditMessage.setAction(action);

        try {
            String uuid = SyncUtils.extractUUIDFromResourceLinks(resourceLinks);
            Object data = syncPersistence.retrieveData(getPreferredClient(), category, uuid);
            syncClient.pushDataToParent(data, resourceLinks, getParentUri());
    
            auditMessage.setSuccess(true);
            auditMessage.setDetails(PUSH_SUCCESS_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Problem with pushing to parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            auditMessage = auditService.saveAuditMessage(auditMessage);
        }
        return auditMessage;
    }

    private String getPreferredClient() {
        return Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);
    }
    
    private String getParentUri() {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }
    
    private String getLocalUri() {
        return configurationService.getSyncConfiguration().getGeneral().getLocalFeedLocation();
    }
    
    private AuditMessage prepareBaseAuditMessage() {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditMessage.setOperation(PUSH_OPERATION);
        return auditMessage;
    }
}
