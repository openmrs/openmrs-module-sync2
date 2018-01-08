package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.sync.SyncPersistence;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PULL_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.*;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private SyncConfigurationService configurationService;

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();
    
    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks,
                                                  String action, String clientName) {
        String resourceURL = getFullUrl(getParentBaseUrl(configurationService), resourceLinks.get(clientName));

        LOGGER.info(String.format("Pull category: %s, address: %s, action: %s", category, resourceURL, action));

        AuditMessage auditMessage = prepareBaseAuditMessage(PULL_OPERATION, configurationService);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(resourceURL);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);
    
        try {
            Object pulledObject = syncClient.pullDataFromParent(category, clientName, resourceURL);
            syncPersistence.persistRetrievedData(pulledObject, action);
        
            auditMessage.setSuccess(true);
            auditMessage.setDetails(PULL_SUCCESS_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Problem with pulling from parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            auditMessage = syncAuditService.saveAuditMessage(auditMessage);
        }
        return auditMessage;
    }
    
    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks,
                                                  String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        return pullDataFromParentAndSave(category, resourceLinks, action, clientName);
    }
}
