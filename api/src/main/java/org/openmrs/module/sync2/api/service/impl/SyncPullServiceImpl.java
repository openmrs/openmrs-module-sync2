package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.filter.impl.PullFilterService;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.service.UnifyService;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
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
import static org.openmrs.module.sync2.api.utils.SimpleObjectSerializationUtils.serialize;

@Component(value = SyncConstants.SYNC_PULL_SERVICE_BEAN)
public class SyncPullServiceImpl extends AbstractSynchronizationService implements SyncPullService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private PullFilterService pullFilterService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private ParentObjectHashcodeService parentObjectHashcodeService;

    @Autowired
    private UnifyService unifyService;

    private static final String FAILED_SYNC_MESSAGE = "Problem with pulling from parent";

    @Override
    public AuditMessage pullAndSaveObjectFromParent(SyncCategory category, Map<String, String> resourceLinks,
                                                  String action, String clientName, String uuid) {
        AuditMessage auditMessage = initSynchronization(category, resourceLinks, action, clientName);
        boolean shouldSynchronize = true;

        try {
            String parentPull = getBaseResourceUrl(resourceLinks, clientName);
            String localPull = getPullUrl(resourceLinks, clientName, CHILD);
            String localPush = getPushUrl(resourceLinks, clientName, CHILD);

            SyncObject pulledObject = new SyncObject(getPulledObject(category, action, clientName, uuid, parentPull));
            pulledObject.setSimpleObject(SyncUtils.isDeleteAction(action) ? null :
                    unifyService.unifyObject(pulledObject.getBaseObject(), category, clientName));
            SyncObject localPulledObject = new SyncObject(syncClient.pullData(category, clientName, localPull, CHILD));
            localPulledObject.setSimpleObject(unifyService.unifyObject(localPulledObject.getBaseObject(), category, clientName));

            shouldSynchronize = pullFilterService.shouldBeSynced(category, pulledObject.getBaseObject(), action)
                && pulledObject.getBaseObject() != null
                && shouldSynchronize(pulledObject.getSimpleObject(), localPulledObject.getSimpleObject(), action);

            if (shouldSynchronize) {
                String hashCode = null;
                if (!SyncUtils.isDeleteAction(action)) {
                    pulledObject.setBaseObject(detectAndResolveConflict(
                            pulledObject, localPulledObject, auditMessage).getBaseObject());
                    hashCode = SyncHashcodeUtils.getHashcode(
                            unifyService.unifyObject(pulledObject.getBaseObject(), category, clientName));
                }
                syncClient.pushData(category, pulledObject.getBaseObject(), clientName, localPush, action, CHILD);
                parentObjectHashcodeService.save(uuid, hashCode);
            }

            auditMessage = successfulMessage(auditMessage, serialize(pulledObject.getSimpleObject()));
        } catch (Error | Exception e) {
            if (SyncUtils.isAuditMessageCategory(category) && SyncUtils.isUnauthorizedException(e)) {
                shouldSynchronize = false;
            } else {
                auditMessage = failedMessage(auditMessage, e);
            }
        } finally {
            if (shouldSynchronize) {
                auditMessage = syncAuditService.saveAuditMessageDuringSync(auditMessage);
            }
        }
        return auditMessage;
    }

    @Override
    public List<AuditMessage> pullAndSaveObjectFromParent(SyncCategory category, String uuid) {
        return synchronizeObject(category, uuid);
    }

    @Override
    protected List<String> determineActionsBasingOnSyncType(Object localObj, Object parentObj) {
        return determineActions(localObj, parentObj);
    }

    @Override
    protected AuditMessage synchronizeObject(SyncCategory category, Map<String, String> resourceLinks, String action,
            String clientName, String uuid) {
        return pullAndSaveObjectFromParent(category, resourceLinks, action, clientName, uuid);
    }

    @Override
    protected SyncOperation getOperation() {
        return SyncOperation.PULL;
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
    public void pullAndSaveObjectsFromParent(SyncCategory category) throws SyncException {
        ParentFeedReader parentFeedReader = ContextUtils.getParentFeedReader();
        parentFeedReader.pullAndProcessFeeds(category);
    }

    @Override
    public AuditMessage pullAndSaveObjectFromParent(SyncCategory category, Map<String, String> resourceLinks,
                                                  String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks, category.getCategory(), getOperation());
        String uuid = extractUUIDFromResourceLinks(resourceLinks, category.getCategory(), clientName);
        return pullAndSaveObjectFromParent(category, resourceLinks, action, clientName, uuid);
    }

    private Object getPulledObject(SyncCategory category, String action, String clientName, String uuid, String parentPull) {
        return pullData(category, action, clientName, uuid, parentPull, PARENT);
    }
}
