package org.openmrs.module.sync2.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.dao.MergeConflictDao;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MergeConflictDaoImpl implements MergeConflictDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public MergeConflict getById(Integer id) {
		return (MergeConflict) getSession()
				.createCriteria(MergeConflict.class)
				.add(Restrictions.eq(SyncConstants.MERGE_CONFLICT_ID_FIELD_NAME, id))
				.uniqueResult();
	}

	@Override
	public MergeConflict getByUuid(String uuid) {
		return (MergeConflict) getSession()
				.createCriteria(MergeConflict.class)
				.add(Restrictions.eq(SyncConstants.MERGE_CONFLICT_UUID_FIELD_NAME, uuid))
				.uniqueResult();
	}

	@Override
	public MergeConflict save(MergeConflict mergeConflict) {
		getSession().saveOrUpdate(mergeConflict);
		return mergeConflict;
	}
}
