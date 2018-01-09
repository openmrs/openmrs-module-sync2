package org.openmrs.module.sync2.api.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.apache.commons.lang3.BooleanUtils;
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
    public AuditMessage saveAuditMessage(AuditMessage auditMessage) {
        boolean persistSuccessAudit = BooleanUtils.isTrue(auditMessage.getSuccess())
                && configuration.getSyncConfiguration().getGeneral().isPersistSuccessAudit();
        boolean persistFailureAudit = BooleanUtils.isFalse(auditMessage.getSuccess())
                && configuration.getSyncConfiguration().getGeneral().isPersistFailureAudit();
        if (persistFailureAudit || persistSuccessAudit) {
            if (auditMessage.getTimestamp() == null) {
                auditMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
            }
            return dao.saveItem(auditMessage);
        }
        return null;
    }

    @Override
    public AuditMessage setNextAudit(AuditMessage current, AuditMessage next) throws APIException {
        if (current == null || next == null) {
            return null;
        }
        current.setNextMessage(next.getId());
        return dao.saveItem(current);
    }

    private String serializeResults(AuditMessageList results) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gsonBuilder.registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer());
        Gson gson = gsonBuilder.create();

        return gson.toJson(results);
    }
}