package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PULL_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getFullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getParentBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.serializeMapToPrettyJson;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getLocalBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushPath;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private SyncConfigurationService configurationService;

    private SyncClient syncClient = new SyncClient();

    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks,
                                                  String action, String clientName) {
        String parentResourceURL = getFullUrl(getParentBaseUrl(configurationService), resourceLinks.get(clientName));
        String localResourceURL = getFullUrl(getLocalBaseUrl(configurationService), getPushPath(resourceLinks.get(clientName)));
        boolean pulledObjectExist = false;
        LOGGER.info(String.format("Pull category: %s, address: %s, action: %s", category, parentResourceURL, action));

        AuditMessage auditMessage = prepareBaseAuditMessage(PULL_OPERATION, configurationService);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(parentResourceURL);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);

        try {
            Object pulledObject = syncClient.pullData(category, clientName, parentResourceURL);
            Object localPulledObject = syncClient.pullData(category, clientName, parentResourceURL);

            pulledObjectExist = pulledObject.equals(localPulledObject);
            if (!pulledObjectExist) {
                syncClient.pushData(pulledObject, clientName, localResourceURL, action);
            }

            auditMessage.setSuccess(true);
            auditMessage.setDetails(PULL_SUCCESS_MESSAGE);

        } catch (Error | Exception e) {
            LOGGER.error("Problem with pulling from parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (!pulledObjectExist)
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
