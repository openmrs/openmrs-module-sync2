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
import java.time.LocalDateTime;
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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer());
        Gson gson = gsonBuilder.create();

        gson.getAdapter(AuditMessage.AuditMessageSerializer.class);
        return gson.toJson(auditMessage);
    }

    @Override
    public String getPaginatedMessages(Integer page, Integer pageSize, Boolean success, String action, String resourceName) throws APIException {
        List<AuditMessage> auditMessageList = dao.getPaginatedMessages(page, pageSize, success, action, resourceName);
        AuditMessageList result = new AuditMessageList(dao.getCountOfMessages(), page, pageSize, auditMessageList);
        return serializeResults(result);
    }

    @Override
    public AuditMessage saveSuccessfulAudit(String resourceName, String resourceUrl, String action, String error) throws APIException {
        if (configuration.getSyncConfiguration().getGeneral().isPersistSuccessAudit()) {
            AuditMessage newItem = new AuditMessage();
            newItem.setSuccess(true);
            newItem.setTimestamp(new Timestamp(System.currentTimeMillis()));
            newItem.setResourceName(resourceName);
            newItem.setResourceUrl(resourceUrl);
            newItem.setAction(action);
            newItem.setError(error);

            return dao.saveItem(newItem);
        }
        return null;
    }

    @Override
    public AuditMessage saveFailedAudit(String resourceName, String resourceUrl, String action, String error) throws APIException {
        if (configuration.getSyncConfiguration().getGeneral().isPersistFailureAudit()) {
            AuditMessage newItem = new AuditMessage();
            newItem.setSuccess(false);
            newItem.setTimestamp(new Timestamp(System.currentTimeMillis()));
            newItem.setResourceName(resourceName);
            newItem.setResourceUrl(resourceUrl);
            newItem.setAction(action);
            newItem.setError(error);

            return dao.saveItem(newItem);
        }
        return null;
    }

    private String serializeResults(AuditMessageList results) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer());
        Gson gson = gsonBuilder.create();

        gson.getAdapter(AuditMessage.AuditMessageSerializer.class);
        return gson.toJson(results);
    }
}