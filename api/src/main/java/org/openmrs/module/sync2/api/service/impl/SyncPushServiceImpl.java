package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.api.filter.impl.PushFilterService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
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

import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;
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
    public AuditMessage readAndPushObjectToParent(String category, Map<String, String> resourceLinks,
                                                String action, String clientName, String uuid) {

        AuditMessage auditMessage = initSynchronization(category, resourceLinks, action, clientName);
        boolean shouldSynchronize = true;

        try {
            String parentPush = getBaseResourceUrl(resourceLinks, clientName);
            String localPull = getPullUrl(resourceLinks, clientName, CHILD);
            String parentPull = getPullUrl(resourceLinks, clientName, PARENT);

            SyncObject localObj2 = new SyncObject(getLocalObject(category, action, clientName, uuid, localPull));
            localObj2.setSimpleObject(unifyService.unifyObject(localObj2.getBaseObject(), category, clientName));
            SyncObject parentObj2 = new SyncObject(syncClient.pullData(category, clientName, parentPull, PARENT));
            parentObj2.setSimpleObject(isDeleteAction(action) ? null :
                    unifyService.unifyObject(parentObj2.getBaseObject(), category, clientName));

            shouldSynchronize = pushFilterService.shouldBeSynced(category, localObj2.getBaseObject(), action)
                    && localObj2.getBaseObject() != null
                    && shouldSynchronize(localObj2.getSimpleObject(), parentObj2.getSimpleObject());

            if (shouldSynchronize) {
                String hashCode = null;
                if (!isDeleteAction(action)) {
                    localObj2.setBaseObject(detectAndResolveConflict(
                            localObj2, parentObj2, auditMessage).getBaseObject());
                    hashCode = SyncHashcodeUtils.getHashcode(
                            unifyService.unifyObject(localObj2.getBaseObject(), category, clientName));
                }
                syncClient.pushData(category, localObj2.getBaseObject(), clientName, parentPush, action, PARENT);
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
    public List<AuditMessage> readAndPushObjectToParent(String category, String uuid) {
        return synchronizeObject(category, uuid);
    }

    @Override
    public void readAndPushObjectsToParent(String category) throws SyncException {
        localFeedReader.readAndPushAllFeeds(category);
    }

    @Override
    protected List<String> determineActionsBasingOnSyncType(Object localObj, Object parentObj) {
        return determineActions(parentObj, localObj);
    }

    @Override
    protected AuditMessage synchronizeObject(String category, Map<String, String> resourceLinks, String action,
            String clientName, String uuid) {
        return readAndPushObjectToParent(category, resourceLinks, action, clientName, uuid);
    }

    @Override
    protected String getOperation() {
        return PUSH_OPERATION;
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
    public AuditMessage readAndPushObjectToParent(String category, Map<String, String> resourceLinks,
            String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        String uuid = extractUUIDFromResourceLinks(resourceLinks);
        return readAndPushObjectToParent(category, resourceLinks, action, clientName, uuid);
    }

    private Object getLocalObject(String category, String action, String clientName, String uuid, String localPull) {
        return pullData(category, action, clientName, uuid, localPull, CHILD);
    }
}
