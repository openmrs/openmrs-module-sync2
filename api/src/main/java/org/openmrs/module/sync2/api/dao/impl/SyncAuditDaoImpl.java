package org.openmrs.module.sync2.api.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.dao.SyncAuditDao;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.audit.PaginatedAuditMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collections;
import java.util.List;


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

    public PaginatedAuditMessages getPaginatedAuditMessages(Integer page, Integer pageSize, Boolean success, String action,
                                                        String resourceName, String creatorInstanceId) {
        Criteria selectCriteria = createSelectCriteria(success, action, resourceName, creatorInstanceId);
        
        Long itemCount = countRows(selectCriteria);
        
        selectCriteria.setFirstResult((page - 1) * pageSize);
        selectCriteria.setMaxResults(pageSize);
        List<AuditMessage> list = Collections.checkedList(selectCriteria.list(), AuditMessage.class);
        
        return new PaginatedAuditMessages(itemCount, page, pageSize, list);
    }

    public Long getCountOfMessages() {
        return countRows(getSession().createCriteria(AuditMessage.class));
    }

    public AuditMessage saveItem(AuditMessage auditMessage) {
        getSession().saveOrUpdate(auditMessage);
        return auditMessage;
    }

    private Criteria createSelectCriteria(Boolean success, String action, String resourceName,
                                          String creatorInstanceId) {
        Criteria selectCriteria = getSession().createCriteria(AuditMessage.class);
        if (success != null) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_STATUS_FIELD_NAME, success));
        }
        if (StringUtils.isNotEmpty(action)) {
            selectCriteria.add(Restrictions.eq(SyncConstants.AUDIT_MESSAGE_ACTION_FIELD_NAME, action));
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