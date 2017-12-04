package org.openmrs.module.sync2.api.db;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("sync2.SyncAuditDao")
public class SyncAuditDao {

    @Autowired
    DbSessionFactory sessionFactory;

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    public AuditMessage getMessageById(Integer id) {
        return (AuditMessage) getSession()
                .createCriteria(AuditMessage.class)
                .add(Restrictions.eq(SyncConstants.ID_COLUMN_NAME, id))
                .uniqueResult();
    }

    public List<AuditMessage> getPaginatedMessages(Integer page, Integer pageSize) {
        return (List<AuditMessage>) getSession()
                .createCriteria(AuditMessage.class)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .list();
    }

    public Long getCountOfMessages() {
        return (Long) getSession()
                .createCriteria(AuditMessage.class)
                .setProjection(Projections.rowCount())
                .list()
                .get(0);
    }

    public AuditMessage saveItem(AuditMessage auditMessage) {
        Session session = sessionFactory.getHibernateSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(auditMessage);
            tx.commit();
        }
        catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
        finally {
            if (session != null) {
                session.close();
            }
        }
        return auditMessage;
    }
}