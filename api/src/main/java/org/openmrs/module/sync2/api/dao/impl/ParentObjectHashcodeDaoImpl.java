package org.openmrs.module.sync2.api.dao.impl;

import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.dao.ParentObjectHashcodeDao;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParentObjectHashcodeDaoImpl implements ParentObjectHashcodeDao {

	@Autowired
	private DbSessionFactory sessionFactory;

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public ParentObjectHashcode getById(Integer id) {
		return (ParentObjectHashcode) getSession()
				.createCriteria(ParentObjectHashcode.class)
				.add(Restrictions.eq(SyncConstants.PARENT_OBJECT_HASHCODE_ID_FIELD_NAME, id))
				.uniqueResult();
	}

	@Override
	public ParentObjectHashcode getByUuid(String uuid) {
		return (ParentObjectHashcode) getSession()
				.createCriteria(ParentObjectHashcode.class)
				.add(Restrictions.eq(SyncConstants.PARENT_OBJECT_HASHCODE_UUID_FIELD_NAME, uuid))
				.uniqueResult();
	}

	@Override
	public ParentObjectHashcode getByObjectUuid(String objectUuid) {
		return (ParentObjectHashcode) getSession()
				.createCriteria(ParentObjectHashcode.class)
				.add(Restrictions.eq(SyncConstants.PARENT_OBJECT_HASHCODE_OBJECT_UUID_FIELD_NAME, objectUuid))
				.uniqueResult();
	}

	@Override
	public ParentObjectHashcode save(ParentObjectHashcode parentObjectHashcode) {
		getSession().saveOrUpdate(parentObjectHashcode);
		return parentObjectHashcode;
	}
}
