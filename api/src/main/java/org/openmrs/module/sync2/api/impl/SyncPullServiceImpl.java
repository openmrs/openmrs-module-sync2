package org.openmrs.module.sync2.api.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.sync.SyncClient;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.RestResourceCreationUtil;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PULL_SUCCESS_MESSAGE;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT_KEY;
import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT_KEY;
import static org.openmrs.module.sync2.SyncConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncAuditUtils.prepareBaseAuditMessage;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPullUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPushUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.serializeMapToPrettyJson;

@Component("sync2.syncPullService")
public class SyncPullServiceImpl implements SyncPullService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullServiceImpl.class);

    @Autowired
    private SyncAuditService syncAuditService;

    private SyncClient syncClient = new SyncClient();

    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks,
                                                  String action, String clientName) {
        String parentPull = getPullUrl(resourceLinks, clientName, PARENT);
        String localPull = getPullUrl(resourceLinks, clientName, CHILD);
        String localPush = getPushUrl(resourceLinks, clientName, CHILD);

        boolean pulledObjectExist = false;
        LOGGER.info(String.format("Pull category: %s, address: %s, action: %s", category, parentPull, action));

        AuditMessage auditMessage = prepareBaseAuditMessage(PULL_OPERATION);
        auditMessage.setResourceName(category);
        auditMessage.setUsedResourceUrl(parentPull);
        auditMessage.setLinkType(clientName);
        auditMessage.setAvailableResourceUrls(serializeMapToPrettyJson(resourceLinks));
        auditMessage.setAction(action);

        try {
            Object pulledObject = syncClient.pullData(category, clientName, parentPull, PARENT);
            Object localPulledObject = getLocalPulledObject(category, clientName, localPull);

            pulledObjectExist = compareLocalAndPulled(clientName, category, pulledObject, localPulledObject);
            if (!pulledObjectExist) {
                syncClient.pushData(pulledObject, clientName, localPush, action, CHILD);
            }

            auditMessage.setSuccess(true);
            auditMessage.setDetails(PULL_SUCCESS_MESSAGE);

        } catch (Error | Exception e) {
            LOGGER.error("Problem with pulling from parent", e);
            auditMessage.setSuccess(false);
            auditMessage.setDetails(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (!pulledObjectExist)
                auditMessage = syncAuditService.saveAuditMessage(auditMessage);
        }
        return auditMessage;
    }

    @Override
    public AuditMessage pullDataFromParentAndSave(String category, Map<String, String> resourceLinks,
                                                  String action) {
        String clientName = SyncUtils.selectAppropriateClientName(resourceLinks);
        return pullDataFromParentAndSave(category, resourceLinks, action, clientName);
    }

    private boolean compareLocalAndPulled(String clientName, String category, Object pulled, Object local) {
        boolean result = false;

        if (null != local) {
            switch (clientName) {
                case REST_CLIENT_KEY:
                    RestResource restLocal = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) local);
                    RestResource restPulled = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) pulled);

                    result = restLocal.equals(restPulled);
                    break;
                case FHIR_CLIENT_KEY:
                    result = category.equals(CATEGORY_PATIENT) ?
                            FHIRPatientUtil.compareCurrentPatients(local, pulled) : local.equals(pulled);
                    break;
                default:
                    result = local.equals(pulled);
            }
        }

        return result;
    }

    private Object getLocalPulledObject(String category, String clientName, String localResourceURL) {
        Object result;

        try {
            result = syncClient.pullData(category, clientName, localResourceURL, CHILD);
        } catch(HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                result = null;
            } else {
                throw new SyncException("Error during reading local object: ", e);
            }
        }

        return result;
    }

}
