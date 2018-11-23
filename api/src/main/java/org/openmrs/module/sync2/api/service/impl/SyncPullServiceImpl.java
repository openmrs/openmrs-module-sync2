package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.filter.impl.PullFilterService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.UnifyService;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushUrl;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl extends AbstractSynchronizationService implements SyncPullService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private PullFilterService pullFilterService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private ParentFeedReader parentFeedReader;

    @Autowired
    private ParentObjectHashcodeService parentObjectHashcodeService;

    @Autowired
    private UnifyService unifyService;

    private static final String FAILED_SYNC_MESSAGE = "Problem with pulling from parent";

    @Override
    public AuditMessage pullAndSaveObjectFromParent(String category, Map<String, String> resourceLinks,
                                                  String action, String clientName, String uuid) {
        AuditMessage auditMessage = initSynchronization(category, resourceLinks, action, clientName);
        boolean shouldSynchronize = true;

        try {
            String parentPull = getBaseResourceUrl(resourceLinks, clientName);
            String localPull = getPullUrl(resourceLinks, clientName, CHILD);
            String localPush = getPushUrl(resourceLinks, clientName, CHILD);

            Object pulledObject = getPulledObject(category, action, clientName, uuid, parentPull);
            Object localPulledObject = syncClient.pullData(category, clientName, localPull, CHILD);
            SimpleObject localObject = unifyService.unifyObject(localPulledObject, category, clientName);
            SimpleObject foreignObject = isDeleteAction(action) ? null :
                    unifyService.unifyObject(pulledObject, category, clientName);

            shouldSynchronize = pullFilterService.shouldBeSynced(category, pulledObject, action)
                && pulledObject != null && shouldSynchronize(foreignObject, localObject);

            if (shouldSynchronize) {
                String hashCode = null;
                if (!isDeleteAction(action)) {
                    pulledObject = detectAndResolveConflict(foreignObject, localObject, auditMessage);
                    hashCode = SyncHashcodeUtils.getHashcode(unifyService.unifyObject(pulledObject, category, clientName));
                }
                syncClient.pushData(category, pulledObject, clientName, localPush, action, CHILD);
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
    public List<AuditMessage> pullAndSaveObjectFromParent(String category, String uuid) {
        return synchronizeObject(category, uuid);
    }

    @Override
    protected List<String> determineActionsBasingOnSyncType(Object localObj, Object parentObj) {
        return determineActions(localObj, parentObj);
    }

    @Override
    protected AuditMessage synchronizeObject(String category, Map<String, String> resourceLinks, String action,
            String clientName, String uuid) {
        return pullAndSaveObjectFromParent(category, resourceLinks, action, clientName, uuid);
    }

    @Override
    protected String getOperation() {
        return PULL_OPERATION;
    }

    @Override
    protected String getBaseResourceUrl(Map<String, String> resourceLinks, String clientName) {
        return getPullUrl(resourceLinks, clientName, PARENT);
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
    public void pullAndSaveObjectsFromParent(String category) throws SyncException {
        parentFeedReader.pullAndProcessFeeds(category);
    }

    @Override
    public AuditMessage pullAndSaveObjectFromParent(String category, Map<String, String> resourceLinks,
                                                  String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        String uuid = extractUUIDFromResourceLinks(resourceLinks);
        return pullAndSaveObjectFromParent(category, resourceLinks, action, clientName, uuid);
    }

    private Object getPulledObject(String category, String action, String clientName, String uuid, String parentPull) {
        return pullData(category, action, clientName, uuid, parentPull, PARENT);
    }
}
