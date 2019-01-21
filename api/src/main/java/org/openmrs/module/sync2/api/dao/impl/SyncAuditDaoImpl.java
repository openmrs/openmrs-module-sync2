package org.openmrs.module.sync2.api.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.dao.SyncAuditDao;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.audit.PaginatedAuditMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Repository
public class SyncAuditDaoImpl implements SyncAuditDao {

    private static final boolean areVoidedEntriesInPaginatedResult = false;

    @Autowired
    private DbSessionFactory sessionFactory;

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    public AuditMessage getMessageByUuid(String uuid) {
        return (AuditMessage) getSession()
                .createCriteria(AuditMessage.class)
                .add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_UUID_FIELD_NAME, uuid))
                .uniqueResult();
    }

    public AuditMessage getMessageById(Integer id) {
        return (AuditMessage) getSession()
                .createCriteria(AuditMessage.class)
                .add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_ID_FIELD_NAME, id))
                .uniqueResult();
    }

    public PaginatedAuditMessages getPaginatedAuditMessages(Integer page, Integer pageSize, Boolean success, String operation,
                                                        String resourceName, String creatorInstanceId) {
        Criteria selectCriteria = createSelectCriteria(success, operation, resourceName, creatorInstanceId);
        
        Long itemCount = countRows(selectCriteria);
        
        selectCriteria.setFirstResult((page - 1) * pageSize);
        selectCriteria.setMaxResults(pageSize);
        List<AuditMessage> list = Collections.checkedList(selectCriteria.list(), AuditMessage.class);
        
        return new PaginatedAuditMessages(itemCount, page, pageSize, list);
    }

    @Override
    public Set<String> getAllCreatorIds() {
        Criteria selectCriteria = getSession().createCriteria(AuditMessage.class)
                .add(Restrictions.isNotNull(SyncConstants.AUDIT_MESSAGE_CREATOR_INSTANCE_ID))
                .setProjection(Projections.distinct(
                        Projections.property(SyncConstants.AUDIT_MESSAGE_CREATOR_INSTANCE_ID)));
        return new HashSet<String>(Collections.checkedList(selectCriteria.list(), String.class));
    }

    public Long getCountOfMessages() {
        return countRows(getSession().createCriteria(AuditMessage.class));
    }

    public AuditMessage saveItem(AuditMessage auditMessage) {
        getSession().saveOrUpdate(auditMessage);
        return auditMessage;
    }

    @Override
    public AuditMessage getMessageByMergeConflictUuid(String uuid) {
        return (AuditMessage) getSession()
                .createCriteria(AuditMessage.class)
                .add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_MERGE_CONFLICT_UUID_NAME, uuid))
                .uniqueResult();
    }

    private Criteria createSelectCriteria(Boolean success, String operation, String resourceName,
                                          String creatorInstanceId) {
        Criteria selectCriteria = getSession().createCriteria(AuditMessage.class);
        if (success != null) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_STATUS_FIELD_NAME, success));
        }
        if (StringUtils.isNotEmpty(operation)) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_OPERATION_FIELD_NAME, operation));
        }
        if (StringUtils.isNotEmpty(resourceName)) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_RESOURCE_FIELD_NAME, resourceName));
        }
        if (StringUtils.isNotEmpty(creatorInstanceId)) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_CREATOR_INSTANCE_ID, creatorInstanceId));
        }
        if (!areVoidedEntriesInPaginatedResult) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_VOIDED_FIELD_NAME, false));
        }

        return selectCriteria;
    }
    
    private Long countRows(Criteria criteria) {
        Long rows = (Long) criteria
                .setProjection(Projections.rowCount())
                .list()
                .get(0);
        // resetting criteria
        criteria.setProjection(null)
                .setResultTransformer(Criteria.ROOT_ENTITY);
        return rows;
    }
}
