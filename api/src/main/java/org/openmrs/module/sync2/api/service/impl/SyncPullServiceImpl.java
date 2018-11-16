package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.filter.impl.PullFilterService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.ACTION_DELETED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_RETIRED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.compareLocalAndPulled;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl extends AbstractSynchronizationService implements SyncPullService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private PullFilterService pullFilterService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private ParentFeedReader parentFeedReader;

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

            Object pulledObject;
            if (action.equalsIgnoreCase(ACTION_VOIDED) || action.equalsIgnoreCase(ACTION_DELETED)
                    || action.equalsIgnoreCase(ACTION_RETIRED)) {
                pulledObject = uuid;
            } else {
                pulledObject = syncClient.pullData(category, clientName, parentPull, PARENT);
            }
            shouldSynchronize = pullFilterService.shouldBeSynced(category, pulledObject, action)
                    && shouldPullObject(pulledObject, category,clientName, localPull);

            if (shouldSynchronize) {
                syncClient.pushData(category, pulledObject, clientName, localPush, action, CHILD);
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

    /**
     *
     * @param   pulledObject the object from the parent instance
     * @param   category the category of the object. Represents name of the object class
     * @param   clientName the name of the used client i.e. rest, fhir
     * @param   url the url to pull local instance of the object
     *
     * @return  true if the parent and local objects are not equal.
     *          false if the objects are equal or pulled object from the parent instance doesn't exists.
     */
    private boolean shouldPullObject(Object pulledObject, String category, String clientName, String url) {
        Object localPulledObject = syncClient.pullData(category, clientName, url, CHILD);
        return pulledObject != null && !compareLocalAndPulled(clientName, category, pulledObject, localPulledObject);
    }
}
