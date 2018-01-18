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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class SyncAuditDaoImpl implements SyncAuditDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    public AuditMessage getMessageByUuid(String uuid) {
        return (AuditMessage) getSession()
                .createCriteria(AuditMessage.class)
                .add(Restrictions.eq(SyncConstants.AUDIT_LOG_UUID_COLUMN_NAME, uuid))
                .uniqueResult();
    }

    public AuditMessage getMessageById(Integer id) {
        return (AuditMessage) getSession()
                .createCriteria(AuditMessage.class)
                .add(Restrictions.eq(SyncConstants.AUDIT_LOG_ID_COLUMN_NAME, id))
                .uniqueResult();
    }

    public List<AuditMessage> getPaginatedMessages(Integer page, Integer pageSize, Boolean success, String action, String resourceName) {
        Criteria selectCriteria = createSelectCriteria(success, action, resourceName);

        selectCriteria.setFirstResult((page - 1) * pageSize);
        selectCriteria.setMaxResults(pageSize);

        return (List<AuditMessage>) selectCriteria.list();
    }

    public Long getCountOfMessages() {
        return (Long) getSession()
                .createCriteria(AuditMessage.class)
                .setProjection(Projections.rowCount())
                .list()
                .get(0);
    }

    public AuditMessage saveItem(AuditMessage auditMessage) {
        getSession().saveOrUpdate(auditMessage);
        return auditMessage;
    }

    private Criteria createSelectCriteria(Boolean success, String action, String resourceName) {
        Criteria selectCriteria = getSession().createCriteria(AuditMessage.class);

        if (success != null) {
            selectCriteria.add(Restrictions.eq(SyncConstants.STATUS_COLUMN_NAME, success));
        }

        if (StringUtils.isNotEmpty(action)) {
            selectCriteria.add(Restrictions.eq(SyncConstants.ACTION_COLUMN_NAME, action));
        }

        if (StringUtils.isNotEmpty(resourceName)) {
            selectCriteria.add(Restrictions.eq(SyncConstants.RESOURCE_NAME_COLUMN_NAME, resourceName));
        }

        return selectCriteria;
    }
}