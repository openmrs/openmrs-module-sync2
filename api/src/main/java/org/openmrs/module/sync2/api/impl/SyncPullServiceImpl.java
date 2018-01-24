package org.openmrs.module.sync2.api.impl;

import org.openmrs.module.sync2.api.SyncAuditService;
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

import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PULL_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.serializeMapToPrettyJson;
import static org.openmrs.module.sync2.api.utils.SyncUtils.compareLocalAndPulled;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private SyncAuditService syncAuditService;

    private SyncClient syncClient = new SyncClient();

    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks,
                                                  String action, String clientName) {
        String parentPull = getPullUrl(resourceLinks, clientName, PARENT);
        String localPull = getPullUrl(resourceLinks, clientName, CHILD);
        String localPush = getPushUrl(resourceLinks, clientName, CHILD);

        boolean pulledObjectExist = false;
        LOGGER.info(String.format("Pull category: %s, address: %s, action: %s", category, parentPull, action));
        String uuid = extractUUIDFromResourceLinks(resourceLinks);


        AuditMessage auditMessage = prepareBaseAuditMessage(PULL_OPERATION);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(parentPull);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);

        try {
            Object pulledObject = action.equals(ACTION_VOIDED) ? uuid : syncClient.pullData(category, clientName, parentPull, PARENT);
            Object localPulledObject = syncClient.pullData(category, clientName, localPull, CHILD);
            pulledObjectExist = pulledObject != null ?
                    compareLocalAndPulled(clientName, category, pulledObject, localPulledObject) : true;

            if (!pulledObjectExist) {
                syncClient.pushData(pulledObject, clientName, localPush, action, CHILD);
            }

            auditMessage.setSuccess(true);
            auditMessage.setDetails(PULL_SUCCESS_MESSAGE);

        } catch (Error | Exception e) {
            LOGGER.error("Problem with pulling from parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (!pulledObjectExist) {
                auditMessage = syncAuditService.saveAuditMessage(auditMessage);
            }
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
