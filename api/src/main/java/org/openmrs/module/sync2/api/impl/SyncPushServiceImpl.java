package org.openmrs.module.sync2.api.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncPushService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PUSH_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.compareLocalAndPulled;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushUrl;

@Component("sync2.syncPushService")
public class SyncPushServiceImpl implements SyncPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushService.class);

    @Autowired
    private SyncAuditService syncAuditService;

    private SyncClient syncClient = new SyncClient();

    @Override
    public AuditMessage readDataAndPushToParent(String category, Map<String, String> resourceLinks,
                                                String action, String clientName) {
        String parentPush = getPushUrl(resourceLinks, clientName, PARENT);
        String localPull = getPullUrl(resourceLinks, clientName, CHILD);
        String parentPull = getPullUrl(resourceLinks, clientName, PARENT);

        boolean pulledObjectExist = false;
        LOGGER.info(String.format("SyncPushService category: %s, address: %s, action: %s", category, parentPush, action));
        String uuid = extractUUIDFromResourceLinks(resourceLinks);
    
        AuditMessage auditMessage = prepareBaseAuditMessage(PUSH_OPERATION);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(parentPush);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(SyncUtils.serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);

        try {
            Object localObj = action.equals(ACTION_VOIDED) ? uuid : syncClient.pullData(category, clientName, localPull, CHILD);
            Object parentObj = syncClient.pullData(category, clientName, parentPull, PARENT);
            pulledObjectExist = localObj != null ?
                    compareLocalAndPulled(clientName, category, localObj, parentObj) : true;

            if (!pulledObjectExist) {
                syncClient.pushData(localObj, clientName, parentPush, action, PARENT);
            }

            auditMessage.setSuccess(true);
            auditMessage.setDetails(PUSH_SUCCESS_MESSAGE);
        } catch (Error | Exception e) {
            LOGGER.error("Problem with pushing to parent", e);
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
    public AuditMessage readDataAndPushToParent(String category, Map<String, String> resourceLinks,
                                                String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        return readDataAndPushToParent(category, resourceLinks, action, clientName);
    }
}
