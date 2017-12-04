package org.openmrs.module.sync2.api.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.db.SyncAuditDao;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.audit.AuditMessageList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SyncAuditServiceImpl extends BaseOpenmrsService implements SyncAuditService {

    SyncAuditDao syncAuditDao;

    @Autowired
    private SyncConfigurationService configuration;

    public void setSyncAuditDao(SyncAuditDao dao) {
        this.syncAuditDao = dao;
    }

    @Override
    public AuditMessage getMessageById(Integer id) throws APIException {
        return syncAuditDao.getMessageById(id);
    }

    @Override
    public String getJsonMessageById(Integer id) throws APIException, JsonParseException {
        AuditMessage auditMessage = syncAuditDao.getMessageById(id);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer());
        Gson gson = gsonBuilder.create();

        gson.getAdapter(AuditMessage.AuditMessageSerializer.class);
        return gson.toJson(auditMessage);
    }

    @Override
    public String getPaginatedMessages(Integer page, Integer pageSize) throws APIException {
        List<AuditMessage> auditMessageList = syncAuditDao.getPaginatedMessages(page, pageSize);
        AuditMessageList result = new AuditMessageList(syncAuditDao.getCountOfMessages(), page, pageSize, auditMessageList);
        return serializeResults(result);
    }

    @Override
    public AuditMessage saveItem(AuditMessage auditMessage) throws APIException {
        if (auditMessage.getSuccess() && configuration.getSyncConfiguration().getGeneral().isPersistSuccessAudit()
                || !auditMessage.getSuccess() && configuration.getSyncConfiguration().getGeneral().isPersistFailureAudit()) {
            return syncAuditDao.saveItem(auditMessage);
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