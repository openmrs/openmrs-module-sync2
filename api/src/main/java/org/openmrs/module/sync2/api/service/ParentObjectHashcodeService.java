package org.openmrs.module.sync2.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;

import javax.transaction.Transactional;

public interface ParentObjectHashcodeService extends OpenmrsService {

	@Transactional
	ParentObjectHashcode getById(Integer id);

	@Transactional
	ParentObjectHashcode getByUuid(String uuid);

	@Transactional
	ParentObjectHashcode getByObjectUuid(String objectUuid);

	@Transactional
	ParentObjectHashcode save(ParentObjectHashcode parentObjectHashcode);

	@Transactional
	void delete(ParentObjectHashcode parentObjectHashcode, String reason);
}
