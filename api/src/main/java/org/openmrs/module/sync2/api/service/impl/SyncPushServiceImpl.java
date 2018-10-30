package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.api.filter.impl.PushFilterService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.compareLocalAndPulled;
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

            Object localObj = action.equals(ACTION_VOIDED) ? uuid : syncClient.pullData(category, clientName, localPull, CHILD);
            shouldSynchronize = pushFilterService.shouldBeSynced(category, localObj, action)
                    && shouldPushObject(localObj, category, clientName, parentPull);

            if (shouldSynchronize) {
                syncClient.pushData(category, localObj, clientName, parentPush, action, PARENT);
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
    public AuditMessage readAndPushObjectToParent(String category, Map<String, String> resourceLinks,
            String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        String uuid = extractUUIDFromResourceLinks(resourceLinks);
        return readAndPushObjectToParent(category, resourceLinks, action, clientName, uuid);
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

    /**
     *
     * @param   localObj the object from the local instance
     * @param   category the category of the object. Represents name of the object class
     * @param   clientName the name of the used client i.e. rest, fhir
     * @param   url the url to pull parent instance of the object
     *
     * @return  true if the parent and local objects are not equal.
     *          false if the objects are equal or pulled object from the local instance doesn't exists.
     */
    private boolean shouldPushObject(Object localObj, String category, String clientName, String url) {
        Object parentObj = syncClient.pullData(category, clientName, url, PARENT);
        return localObj != null && !compareLocalAndPulled(clientName, category, localObj, parentObj);
    }
}
