package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sync2.api.dao.MergeConflictDao;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("sync2.mergeConflictService")
public class MergeConflictServiceImpl extends BaseOpenmrsService implements MergeConflictService {

	@Autowired
	private MergeConflictDao mergeConflictDao;

	@Override
	public MergeConflict getById(Integer id) {
		return mergeConflictDao.getById(id);
	}

	@Override
	public MergeConflict getByUuid(String uuid) {
		return mergeConflictDao.getByUuid(uuid);
	}

	@Override
	public MergeConflict save(MergeConflict mergeConflict) {
		return mergeConflictDao.save(mergeConflict);
	}

	@Override
	public void delete(MergeConflict mergeConflict, String reason) {
		mergeConflict.setVoided(true);
		mergeConflict.setVoidedBy(Context.getAuthenticatedUser());
		mergeConflict.setDateVoided(new Date());
		mergeConflict.setVoidReason(reason);
		mergeConflictDao.save(mergeConflict);
	}
}
