package org.openmrs.module.sync2.api.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.api.filter.impl.PushFilterService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.AUDIT_MESSAGE_UUID_FIELD_NAME;
import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PUSH_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_URL_FORMAT;
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
    private PushFilterService pushFilterService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private LocalFeedReader localFeedReader;

    @Autowired
    private FeedConfigurationService feedConfigurationService;

    private SyncClient syncClient = new SyncClient();

    @Override
    public AuditMessage readAndPushObjectToParent(String category, Map<String, String> resourceLinks,
                                                String action, String clientName, String uuid) {
        SyncConfigurationUtils.checkIfConfigurationIsValid();

        String parentPush = getPushUrl(resourceLinks, clientName, PARENT);
        String localPull = getPullUrl(resourceLinks, clientName, CHILD);
        String parentPull = getPullUrl(resourceLinks, clientName, PARENT);

        boolean pushToTheParent = true;
        LOGGER.info(String.format("SyncPushService category: %s, address: %s, action: %s", category, parentPush, action));
    
        AuditMessage auditMessage = prepareBaseAuditMessage(PUSH_OPERATION);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(parentPush);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(SyncUtils.prettySerialize(resourceLinks));
        auditMessage.setAction(action);

        try {
            Object localObj = action.equals(ACTION_VOIDED) ? uuid : syncClient.pullData(category, clientName, localPull, CHILD);
            pushToTheParent = pushFilterService.shouldBeSynced(category, localObj, action)
                    && shouldPushObject(localObj, category, clientName, parentPull);

            if (pushToTheParent) {
                syncClient.pushData(localObj, clientName, parentPush, action, PARENT);
            }

            auditMessage.setSuccess(true);
            auditMessage.setDetails(PUSH_SUCCESS_MESSAGE);
        } catch (Error | Exception e) {
            LOGGER.error("Problem with pushing to parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (pushToTheParent) {
                auditMessage = syncAuditService.saveAuditMessageDuringSync(auditMessage);
            }
        }
        return auditMessage;
    }

    @Override
    public List<AuditMessage> readAndPushObjectToParent(String category, String uuid) {
        SyncConfigurationUtils.checkIfConfigurationIsValid();

        FeedConfiguration configuration = feedConfigurationService.getFeedConfigurationByCategory(category);

        Map<String, String> resourceLinks = configuration.getLinkTemplates();
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        List<String> actions = determineActions(category, uuid);

        Map<String, String> mappedResourceLinks = includeUuidInResourceLinks(resourceLinks, uuid);
        List<AuditMessage> result = new ArrayList<>();
        for (String action : actions) {
            result.add(readAndPushObjectToParent(category, mappedResourceLinks, action, clientName, uuid));
        }

        return result;
    }

    private Map<String, String> includeUuidInResourceLinks(Map<String, String> resourceLinks, String uuid) {
        Map<String, String> mappedResourceLinks = new HashMap<>();
        for (Map.Entry<String, String> pair : resourceLinks.entrySet()) {
            mappedResourceLinks.put(pair.getKey(), pair.getValue().replace("{" + AUDIT_MESSAGE_UUID_FIELD_NAME + "}", uuid));
        }
        return mappedResourceLinks;
    }

    private List<String> determineActions(String category, String uuid) {
        String restUrl = String.format(REST_URL_FORMAT, uuid);
        String localPullUrl = SyncUtils.getFullUrl(SyncUtils.getLocalBaseUrl(), restUrl);
        String parentPullUrl = SyncUtils.getFullUrl(SyncUtils.getParentBaseUrl(), restUrl);

        Object localObj = syncClient.pullData(category, REST_CLIENT, localPullUrl, CHILD);
        Object parentObj = syncClient.pullData(category, REST_CLIENT, parentPullUrl, PARENT);

        List<String> result = new ArrayList<>();
        if (parentObj == null) {
            result.add(ACTION_CREATED);
            if (localObj instanceof BaseOpenmrsData && ((BaseOpenmrsData)localObj).isVoided()) {
                result.add(ACTION_VOIDED);
            }
        } else {
            result.add(ACTION_UPDATED);
        }
        return result;
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
