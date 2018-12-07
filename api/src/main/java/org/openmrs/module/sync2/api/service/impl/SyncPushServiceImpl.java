package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.filter.impl.PushFilterService;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.api.service.UnifyService;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushUrl;

@Component("sync2.syncPushService")
public class SyncPushServiceImpl extends AbstractSynchronizationService implements SyncPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushService.class);

    @Autowired
    private PushFilterService pushFilterService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private LocalFeedReader localFeedReader;

    @Autowired
    private ParentObjectHashcodeService parentObjectHashcodeService;

    @Autowired
    private UnifyService unifyService;

    private static final String FAILED_SYNC_MESSAGE = "Problem with pushing to parent";

    @Override
    public AuditMessage readAndPushObjectToParent(CategoryEnum category, Map<String, String> resourceLinks,
                                                String action, String clientName, String uuid) {

        AuditMessage auditMessage = initSynchronization(category, resourceLinks, action, clientName);
        boolean shouldSynchronize = true;

        try {
            String parentPush = getBaseResourceUrl(resourceLinks, clientName);
            String localPull = getPullUrl(resourceLinks, clientName, CHILD);
            String parentPull = getPullUrl(resourceLinks, clientName, PARENT);

            SyncObject localObj = new SyncObject(getLocalObject(category, action, clientName, uuid, localPull));
            localObj.setSimpleObject(unifyService.unifyObject(localObj.getBaseObject(), category, clientName));
            SyncObject parentObj = new SyncObject(syncClient.pullData(category, clientName, parentPull, PARENT));
            parentObj.setSimpleObject(isDeleteAction(action) ? null :
                    unifyService.unifyObject(parentObj.getBaseObject(), category, clientName));

            shouldSynchronize = pushFilterService.shouldBeSynced(category, localObj.getBaseObject(), action)
                    && localObj.getBaseObject() != null
                    && shouldSynchronize(localObj.getSimpleObject(), parentObj.getSimpleObject());

            if (shouldSynchronize) {
                String hashCode = null;
                if (!isDeleteAction(action)) {
                    localObj.setBaseObject(detectAndResolveConflict(
                            localObj, parentObj, auditMessage).getBaseObject());
                    hashCode = SyncHashcodeUtils.getHashcode(
                            unifyService.unifyObject(localObj.getBaseObject(), category, clientName));
                }
                syncClient.pushData(category, localObj.getBaseObject(), clientName, parentPush, action, PARENT);
                parentObjectHashcodeService.save(uuid, hashCode);
            }

            auditMessage = successfulMessage(auditMessage);
        } catch (Error | Exception e) {
            auditMessage = failedMessage(auditMessage, e);
        } finally {
            if (shouldSynchronize) {
                auditMessage = syncAuditService.saveAuditMessageDuringSync(auditMessage);
            }
        }
        return auditMessage;
    }

    @Override
    public AuditMessage mergeForcePush(Object merged, CategoryEnum category, Map<String, String> resourceLinks,
            String action, String uuid) {

        AuditMessage auditMessage = initSynchronization(category, resourceLinks, action, SyncConstants.REST_CLIENT);
        AuditMessage parentAudit = forcePush(merged, category, resourceLinks, action, SyncConstants.REST_CLIENT, PARENT);
        AuditMessage childAudit = forcePush(merged, category, resourceLinks, action, SyncConstants.REST_CLIENT, CHILD);

        if (parentAudit.getSuccess()) {
            try {
                String hashCode = SyncHashcodeUtils.getHashcode(unifyService.unifyObject(merged, category,
                        SyncConstants.REST_CLIENT));
                parentObjectHashcodeService.save(uuid, hashCode);
            } catch (Error | Exception e) {
                return syncAuditService.saveAuditMessage(failedMessage(auditMessage, e));
            }
        }

        return syncAuditService.saveAuditMessage(combineForceAuditMessages(auditMessage, parentAudit, childAudit));
    }

    @Override
    public List<AuditMessage> readAndPushObjectToParent(CategoryEnum category, String uuid) {
        return synchronizeObject(category, uuid);
    }

    @Override
    public void readAndPushObjectsToParent(CategoryEnum category) throws SyncException {
        localFeedReader.readAndPushAllFeeds(category);
    }

    @Override
    protected List<String> determineActionsBasingOnSyncType(Object localObj, Object parentObj) {
        return determineActions(parentObj, localObj);
    }

    @Override
    protected AuditMessage synchronizeObject(CategoryEnum category, Map<String, String> resourceLinks, String action,
            String clientName, String uuid) {
        return readAndPushObjectToParent(category, resourceLinks, action, clientName, uuid);
    }

    @Override
    protected SyncOperation getOperation() {
        return SyncOperation.PUSH;
    }

    @Override
    protected String getBaseResourceUrl(Map<String, String> resourceLinks, String clientName) {
        return getPushUrl(resourceLinks, clientName, PARENT);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected String getFailedSynchronizationMessage() {
        return FAILED_SYNC_MESSAGE;
    }

    @Override
    public AuditMessage readAndPushObjectToParent(CategoryEnum category, Map<String, String> resourceLinks,
            String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks, category.getCategory(), getOperation());
        String uuid = extractUUIDFromResourceLinks(resourceLinks);
        return readAndPushObjectToParent(category, resourceLinks, action, clientName, uuid);
    }

    private Object getLocalObject(CategoryEnum category, String action, String clientName, String uuid, String localPull) {
        return pullData(category, action, clientName, uuid, localPull, CHILD);
    }

    private AuditMessage forcePush(Object merged, CategoryEnum category, Map<String, String> resourceLinks,
            String action, String clientName, OpenMRSSyncInstance instance) {
        AuditMessage message = initSynchronization(category, resourceLinks, action, SyncConstants.REST_CLIENT);
        try {
            syncClient.pushData(
                    category,
                    merged,
                    clientName,
                    getPushUrl(resourceLinks, clientName, instance),
                    action,
                    instance);
            message = successfulMessage(message);
        } catch (Error | Exception e) {
            message = failedMessage(message, e);
        }

        return syncAuditService.saveAuditMessageDuringSync(message);
    }

    private AuditMessage combineForceAuditMessages(AuditMessage combined, AuditMessage msg,  AuditMessage msg2) {
        if (msg.getSuccess()) {
            if (msg2.getSuccess()) {
                combined = successfulMessage(combined);
            } else {
                combined.setDetails(msg2.getDetails());
                combined.setSuccess(false);
            }
        } else {
            StringBuilder sb = new StringBuilder(msg.getDetails());
            if (!msg2.getSuccess()) {
                sb.append(msg2.getDetails());
            }
            combined.setDetails(sb.toString());
            combined.setSuccess(false);
        }
        return combined;
    }
}
