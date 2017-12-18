package org.openmrs.module.sync2.api.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.dao.SyncAuditDao;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.audit.AuditMessageList;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.List;

public class SyncAuditServiceImpl extends BaseOpenmrsService implements SyncAuditService {

    private SyncAuditDao dao;

    @Autowired
    private SyncConfigurationService configuration;

    public void setDao(SyncAuditDao dao) {
        this.dao = dao;
    }

    @Override
    public AuditMessage getMessageById(Integer id) throws APIException {
        return dao.getMessageById(id);
    }

    @Override
    public String getJsonMessageById(Integer id) throws APIException, JsonParseException {
        AuditMessage auditMessage = dao.getMessageById(id);

        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gsonBuilder.registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer());
        Gson gson = gsonBuilder.create();

        return gson.toJson(auditMessage);
    }

    @Override
    public String getPaginatedMessages(Integer page, Integer pageSize, Boolean success, String operation, String resourceName) throws APIException {
        List<AuditMessage> auditMessageList = dao.getPaginatedMessages(page, pageSize, success, operation, resourceName);
        AuditMessageList result = new AuditMessageList(dao.getCountOfMessages(), page, pageSize, auditMessageList);
        return serializeResults(result);
    }

    @Override
    public AuditMessage saveSuccessfulAudit(String resourceName, String resourceUrl, String operation, String details) throws APIException {
        if (configuration.getSyncConfiguration().getGeneral().isPersistSuccessAudit()) {
            AuditMessage newItem = new AuditMessage();
            newItem.setSuccess(true);
            newItem.setTimestamp(new Timestamp(System.currentTimeMillis()));
            newItem.setResourceName(resourceName);
            newItem.setUsedResourceUrl(resourceUrl);
            newItem.setOperation(operation);
            newItem.setDetails(details);

            return dao.saveItem(newItem);
        }
        return null;
    }

    @Override
    public AuditMessage saveFailedAudit(String resourceName, String resourceUrl, String operation, String details) throws APIException {
        if (configuration.getSyncConfiguration().getGeneral().isPersistFailureAudit()) {
            AuditMessage newItem = new AuditMessage();
            newItem.setSuccess(false);
            newItem.setTimestamp(new Timestamp(System.currentTimeMillis()));
            newItem.setResourceName(resourceName);
            newItem.setUsedResourceUrl(resourceUrl);
            newItem.setOperation(operation);
            newItem.setDetails(details);

            return dao.saveItem(newItem);
        }
        return null;
    }
    
    @Override
    public AuditMessage saveAuditMessage(AuditMessage auditMessage) {
        boolean persistSuccessAudit = auditMessage.getSuccess()
                && configuration.getSyncConfiguration().getGeneral().isPersistSuccessAudit();
        boolean persistFailureAudit = !auditMessage.getSuccess()
                && configuration.getSyncConfiguration().getGeneral().isPersistFailureAudit();
        if (persistFailureAudit || persistSuccessAudit) {
            if (auditMessage.getTimestamp() == null) {
                auditMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
            }
            return dao.saveItem(auditMessage);
        }
        return null;
    }
    
    private String serializeResults(AuditMessageList results) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gsonBuilder.registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer());
        Gson gson = gsonBuilder.create();

        return gson.toJson(results);
    }
}