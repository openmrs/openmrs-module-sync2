package org.openmrs.module.sync2.api.service;

import com.google.gson.JsonParseException;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface SyncAuditService extends OpenmrsService {

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(readOnly = true)
    AuditMessage getMessageByUuid(String uuid) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(readOnly = true)
    AuditMessage getMessageById(Integer id) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(readOnly = true)
    String getJsonMessageByUuid(String uuid) throws APIException, JsonParseException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(readOnly = true)
    String getJsonMessageById(Integer id) throws APIException, JsonParseException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(readOnly = true)
    String getPaginatedMessages(Integer page, Integer pageSize, Boolean success, String operation,
                                String resourceName, String creatorInstanceId) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    AuditMessage saveAuditMessageDuringSync(AuditMessage auditMessage) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional
    AuditMessage saveAuditMessage(AuditMessage auditMessage) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional
    AuditMessage setNextAudit(AuditMessage current, AuditMessage next) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional
    Set<String> getAllCreatorIds() throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    @Transactional(readOnly = true)
    AuditMessage getMessageByMergeConflictUuid(String uuid) throws APIException;

    @Authorized(SyncModuleConfig.SYNC_AUDIT_PRIVILEGE)
    String getJsonMessage(AuditMessage message) throws APIException, JsonParseException;
}
