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

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);
   
    @Autowired
    private SyncConfigurationService configurationService;
    
    @Autowired
    private SyncAuditService syncAuditService;

    private SyncClient syncClient = new SyncClient();
    private SyncPersistence syncPersistence = new SyncPersistence();
    
    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks, String baseAddress,
                                                  String action, String clientName) {
        LOGGER.info(String.format("Pull category: %s, address: %s, action: %s", category, baseAddress, action));
    
        String pullUrl = getPullUrl(baseAddress, resourceLinks.get(clientName));
        
        AuditMessage auditMessage = prepareBaseAuditMessage();
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(pullUrl);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(SyncUtils.serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);
    
        try {
            Object pulledObject = syncClient.pullDataFromParent(category, clientName, pullUrl);
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
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks, String baseAddress,
                                                  String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        return pullDataFromParentAndSave(category, resourceLinks, baseAddress, action, clientName);
    }
    
    private String getPullUrl(String baseAddress, String resourceLink) {
        return baseAddress + resourceLink;
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
        auditMessage.setOperation(PULL_OPERATION);
        auditMessage.setParentUrl(getParentUri());
        auditMessage.setLocalUrl(getLocalUri());
        return auditMessage;
    }
}
