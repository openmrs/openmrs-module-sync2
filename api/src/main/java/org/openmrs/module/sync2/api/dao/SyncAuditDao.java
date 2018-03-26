package org.openmrs.module.sync2.api.dao;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.audit.PaginatedAuditMessages;

import java.util.Set;


public interface SyncAuditDao {

    AuditMessage getMessageByUuid(String uuid);

    AuditMessage getMessageById(Integer id);
    
    PaginatedAuditMessages getPaginatedAuditMessages(Integer page, Integer pageSize, Boolean success, String operation,
                                                     String resourceName, String creatorInstanceId);

    Set<String> getAllCreatorIds();

    Long getCountOfMessages();

    AuditMessage saveItem(AuditMessage auditMessage);
}