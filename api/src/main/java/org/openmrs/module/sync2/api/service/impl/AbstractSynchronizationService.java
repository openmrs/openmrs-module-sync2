package org.openmrs.module.sync2.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.conflict.ConflictDetection;
import org.openmrs.module.sync2.api.exceptions.MergeConflictException;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.api.model.enums.SyncOperation;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
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
import static org.openmrs.module.sync2.SyncConstants.SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.prettySerialize;

public abstract class AbstractSynchronizationService {

    protected SyncClient syncClient = new SyncClient();

    @Autowired
    private ConflictDetection conflictDetection;

    @Autowired
    private MergeConflictService mergeConflictService;

    @Autowired
    private MergeConflictMapper mergeConflictMapper;

    private static final String INITIAL_INFO_FORMAT = "%s category: %s, address: %s, action: %s";

    protected abstract List<String> determineActionsBasingOnSyncType(Object localObj, Object parentObj);

    protected abstract AuditMessage synchronizeObject(SyncCategory category, Map<String, String> resourceLinks,
            String action, String clientName, String uuid);

    protected abstract SyncOperation getOperation();

    protected abstract String getBaseResourceUrl(Map<String, String> resourceLinks, String clientName);

    protected abstract Logger getLogger();

    protected abstract String getFailedSynchronizationMessage();

    protected AuditMessage initSynchronization(SyncCategory category, Map<String, String> resourceLinks,
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

    protected List<AuditMessage> synchronizeObject(SyncCategory category, String uuid) {
        SyncConfigurationUtils.checkIfConfigurationIsValid();

        EventConfiguration configuration = ContextUtils.getEventConfigurationService().getEventConfigurationByCategory(
                category);

        Map<String, String> resourceLinks = configuration.getLinkTemplates();
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks, category.getCategory(), getOperation());
        Map<String, String> mappedResourceLinks = includeUuidInResourceLinks(resourceLinks, uuid);

        String restUrl = mappedResourceLinks.get(REST_CLIENT);
        List<String> actions = determineActions(category, restUrl);

        List<AuditMessage> result = new ArrayList<>();
        for (String action : actions) {
            result.add(synchronizeObject(category, mappedResourceLinks, action, clientName, uuid));
        }

        return result;
    }

    protected List<String> determineActions(Object objToUpdate, Object baseObj) {
        List<String> result = new ArrayList<>();
        if (baseObj instanceof BaseOpenmrsData && ((BaseOpenmrsData) baseObj).isVoided()) {
            result.add(ACTION_VOIDED);
        }
        if (objToUpdate == null) {
            result.add(0, ACTION_CREATED);
        } else {
            result.add(0, ACTION_UPDATED);
        }

        return result;
    }

    protected boolean shouldSynchronize(SimpleObject oldObject, SimpleObject newObject, String action) {
        if (isWrongUpdateOrDeleteAction(newObject, action) || isWrongCreateAction(oldObject, newObject, action)) {
            return false;
        }
        return oldObject == null || newObject == null || areDifferentObjects(oldObject, newObject);
    }

    private boolean isWrongCreateAction(SimpleObject oldObject, SimpleObject newObject, String action) {
        return oldObject != null && newObject != null && SyncUtils.isCreateAction(action);
    }

    private boolean isWrongUpdateOrDeleteAction(SimpleObject newObject, String action) {
        return newObject == null && (SyncUtils.isDeleteAction(action) || SyncUtils.isUpdateAction(action));
    }

    private boolean areDifferentObjects(SimpleObject oldObject, SimpleObject newObject) {
        String localHashCode = SyncHashcodeUtils.getHashcode(oldObject);
        String pulledHashCode = SyncHashcodeUtils.getHashcode(newObject);
        return (StringUtils.isNotBlank(localHashCode) && StringUtils.isNotBlank(pulledHashCode)
                && !localHashCode.equalsIgnoreCase(pulledHashCode));
    }

    protected SyncObject detectAndResolveConflict(SyncObject oldObject, SyncObject newObject, AuditMessage auditMessage)
            throws MergeConflictException {
        boolean conflict = conflictDetection.detectConflict(oldObject.getSimpleObject(), newObject.getSimpleObject());
        if (conflict) {
            MergeResult result = SyncUtils.getMergeBehaviour().resolveDiff(SyncObject.class, oldObject, newObject);
            if (result instanceof MergeSuccess) {
                oldObject.setBaseObject(((MergeSuccess) result).getMerged());
            } else if (result instanceof MergeConflict){
                org.openmrs.module.sync2.api.model.MergeConflict mergeConflict = mergeConflictService
                        .save(mergeConflictMapper.map((MergeConflict) result));
                auditMessage.setMergeConflictUuid(mergeConflict.getUuid());
                throw new MergeConflictException("Detected merge conflict which couldn't be resolved automatically.");
            }
        }
        return oldObject;
    }

    protected Object pullData(SyncCategory category, String action, String clientName, String uuid,
            String pullUrl, OpenMRSSyncInstance instance) {
        Object pulledObject;
        if (SyncUtils.isDeleteAction(action)) {
            pulledObject = uuid;
        } else {
            pulledObject = syncClient.pullData(category, clientName, pullUrl, instance);
        }
        return pulledObject;
    }

    private void logInitialInfo(SyncCategory category, String action, Map<String, String> resourceLinks, String clientName) {
        getLogger().info(String.format(INITIAL_INFO_FORMAT,
                this.getClass().getSimpleName(), category, getBaseResourceUrl(resourceLinks, clientName), action));
    }

    private AuditMessage prepareAuditMessage(SyncCategory category, String clientName,
            Map<String, String> resourceLinks, String action) {
        AuditMessage auditMessage = prepareBaseAuditMessage(getOperation().getValue(), clientName);
        auditMessage.setResourceName(category.getCategory());
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

    private List<String> determineActions(SyncCategory category, String restUrl) {
        String localPullUrl = SyncUtils.getFullUrl(SyncUtils.getLocalBaseUrl(), restUrl);
        String parentPullUrl = SyncUtils.getFullUrl(SyncUtils.getParentBaseUrl(SyncConstants.REST_CLIENT), restUrl);

        Object localObj = syncClient.pullData(category, SyncConstants.REST_CLIENT, localPullUrl, CHILD);
        Object parentObj = syncClient.pullData(category, SyncConstants.REST_CLIENT, parentPullUrl, PARENT);

        return determineActionsBasingOnSyncType(localObj, parentObj);
    }

}
