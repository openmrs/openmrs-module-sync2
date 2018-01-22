package org.openmrs.module.sync2.api.dao;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.List;


public interface SyncAuditDao {

    AuditMessage getMessageByUuid(String uuid);

    AuditMessage getMessageById(Integer id);

    List<AuditMessage> getPaginatedMessages(Integer page, Integer pageSize, Boolean success, String action,
                                            String resourceName, String creatorInstanceId);

    Long getCountOfMessages();

    AuditMessage saveItem(AuditMessage auditMessage);
}