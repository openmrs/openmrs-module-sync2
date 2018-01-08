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
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.*;

@Component("sync2.syncPushService")
public class SyncPushServiceImpl implements SyncPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushService.class);

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private SyncConfigurationService configurationService;

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();

    @Override
    public AuditMessage readDataAndPushToParent(String category, Map<String, String> resourceLinks,
                                                String action, String clientName) {
        String resourceURL = getFullUrl(getParentBaseUrl(configurationService), getPushPath(resourceLinks.get(clientName)));

        LOGGER.info(String.format("SyncPushService category: %s, address: %s, action: %s", category, resourceURL, action));
        
        String uuid = extractUUIDFromResourceLinks(resourceLinks);
    
        AuditMessage auditMessage = prepareBaseAuditMessage(PUSH_OPERATION, configurationService);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(resourceURL);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(SyncUtils.serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);
        try {
            Object data = syncPersistence.retrieveData(clientName, category, uuid);
            syncClient.pushDataToParent(data, clientName, resourceURL);
        
            auditMessage.setSuccess(true);
            auditMessage.setDetails(PUSH_SUCCESS_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Problem with pushing to parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            auditMessage = syncAuditService.saveAuditMessage(auditMessage);
        }
        return auditMessage;
    }
    
    @Override
    public AuditMessage readDataAndPushToParent(String category, Map<String, String> resourceLinks,
                                                String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        return readDataAndPushToParent(category, resourceLinks, action, clientName);
    }
}
