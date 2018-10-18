package org.openmrs.module.sync2.api.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.AUDIT_MESSAGE_UUID_FIELD_NAME;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_URL_FORMAT;
import static org.openmrs.module.sync2.SyncConstants.SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.prettySerialize;

public abstract class AbstractSynchronizationService {

    protected SyncClient syncClient = new SyncClient();

    @Autowired
    private FeedConfigurationService feedConfigurationService;

    private static final String INITIAL_INFO_FORMAT = "%s category: %s, address: %s, action: %s";

    protected abstract List<String> determineActionsBasingOnSyncType(Object localObj, Object parentObj);

    protected abstract AuditMessage synchronizeObject(String category, Map<String, String> resourceLinks,
            String action, String clientName, String uuid);

    protected abstract String getOperation();

    protected abstract String getBaseResourceUrl(Map<String, String> resourceLinks, String clientName);

    protected abstract Logger getLogger();

    protected abstract String getFailedSynchronizationMessage();

    protected AuditMessage initSynchronization(String category, Map<String, String> resourceLinks,
            String action, String clientName) {
        SyncConfigurationUtils.checkIfConfigurationIsValid();
        logInitialInfo(category, action, resourceLinks, clientName);
        return prepareAuditMessage(category, clientName, resourceLinks, action);
    }

    protected AuditMessage successfulMessage(AuditMessage base) {
        base.setSuccess(true);
        base.setDetails(SUCCESS_MESSAGE);
        return base;
    }

    protected AuditMessage failedMessage(AuditMessage base, Throwable e) {
        getLogger().error(getFailedSynchronizationMessage(), e);
        base.setSuccess(false);
        base.setDetails(ExceptionUtils.getFullStackTrace(e));
        return base;
    }

    protected List<AuditMessage> synchronizeObject(String category, String uuid) {
        SyncConfigurationUtils.checkIfConfigurationIsValid();

        FeedConfiguration configuration = feedConfigurationService.getFeedConfigurationByCategory(category);

        Map<String, String> resourceLinks = configuration.getLinkTemplates();
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        List<String> actions = determineActions(category, uuid);

        Map<String, String> mappedResourceLinks = includeUuidInResourceLinks(resourceLinks, uuid);
        List<AuditMessage> result = new ArrayList<>();
        for (String action : actions) {
            result.add(synchronizeObject(category, mappedResourceLinks, action, clientName, uuid));
        }

        return result;
    }

    protected List<String> determineActions(Object objToUpdate, Object baseObj) {
        List<String> result = new ArrayList<>();
        if (objToUpdate == null) {
            result.add(ACTION_CREATED);
            if (baseObj instanceof BaseOpenmrsData && ((BaseOpenmrsData) baseObj).isVoided()) {
                result.add(ACTION_VOIDED);
            }
        } else {
            result.add(ACTION_UPDATED);
        }

        return result;
    }

    private void logInitialInfo(String category, String action, Map<String, String> resourceLinks, String clientName) {
        getLogger().info(String.format(INITIAL_INFO_FORMAT,
                this.getClass().getSimpleName(), category, getBaseResourceUrl(resourceLinks, clientName), action));
    }

    private AuditMessage prepareAuditMessage(String category, String clientName,
            Map<String, String> resourceLinks, String action) {
        AuditMessage auditMessage = prepareBaseAuditMessage(getOperation());
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(getBaseResourceUrl(resourceLinks, clientName));
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(prettySerialize(resourceLinks));
        auditMessage.setAction(action);
        return auditMessage;
    }

    private Map<String, String> includeUuidInResourceLinks(Map<String, String> resourceLinks, String uuid) {
        Map<String, String> mappedResourceLinks = new HashMap<>();
        for (Map.Entry<String, String> pair : resourceLinks.entrySet()) {
            mappedResourceLinks.put(pair.getKey(), pair.getValue().replace("{" + AUDIT_MESSAGE_UUID_FIELD_NAME + "}", uuid));
        }
        return mappedResourceLinks;
    }

    private List<String> determineActions(String category, String uuid) {
        String restUrl = String.format(REST_URL_FORMAT, category, uuid);
        String localPullUrl = SyncUtils.getFullUrl(SyncUtils.getLocalBaseUrl(), restUrl);
        String parentPullUrl = SyncUtils.getFullUrl(SyncUtils.getParentBaseUrl(), restUrl);

        Object localObj = syncClient.pullData(category, REST_CLIENT, localPullUrl, CHILD);
        Object parentObj = syncClient.pullData(category, REST_CLIENT, parentPullUrl, PARENT);

        return determineActionsBasingOnSyncType(localObj, parentObj);
    }

}
