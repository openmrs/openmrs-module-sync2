package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sync2.api.dao.ParentObjectHashcodeDao;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("sync2.parentObjectHashcodeService")
public class ParentObjectHashcodeServiceImpl extends BaseOpenmrsService implements ParentObjectHashcodeService {

	@Autowired
	private ParentObjectHashcodeDao parentObjectHashcodeDao;

	@Override
	public ParentObjectHashcode getById(Integer id) {
		return parentObjectHashcodeDao.getById(id);
	}

	@Override
	public ParentObjectHashcode getByUuid(String uuid) {
		return parentObjectHashcodeDao.getByUuid(uuid);
	}

	@Override
	public ParentObjectHashcode getByObjectUuid(String objectUuid) {
		return parentObjectHashcodeDao.getByObjectUuid(objectUuid);
	}

	@Override
	public ParentObjectHashcode save(ParentObjectHashcode newParentObjectHashcode) {
		ParentObjectHashcode parentObjectHashcode = getByObjectUuid(newParentObjectHashcode.getObjectUuid());
		if (parentObjectHashcode != null) {
			parentObjectHashcode.setHashcode(newParentObjectHashcode.getHashcode());
		} else {
			parentObjectHashcode = newParentObjectHashcode;
		}
		return parentObjectHashcodeDao.save(parentObjectHashcode);
	}

	@Override
	public void delete(ParentObjectHashcode parentObjectHashcode, String reason) {
		parentObjectHashcode.setVoided(true);
		parentObjectHashcode.setVoidedBy(Context.getAuthenticatedUser());
		parentObjectHashcode.setDateVoided(new Date());
		parentObjectHashcode.setVoidReason(reason);
		parentObjectHashcodeDao.save(parentObjectHashcode);
	}
}
