package org.openmrs.module.sync2.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.springframework.transaction.annotation.Transactional;

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
	ParentObjectHashcode save(String uuid, String hashCode);

	@Transactional
	void delete(ParentObjectHashcode parentObjectHashcode, String reason);
}
