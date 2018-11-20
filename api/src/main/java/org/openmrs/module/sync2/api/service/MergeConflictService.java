package org.openmrs.module.sync2.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.springframework.transaction.annotation.Transactional;

public interface MergeConflictService extends OpenmrsService {

	@Transactional
	MergeConflict getById(Integer id);

	@Transactional
	MergeConflict getByUuid(String uuid);

	@Transactional
	MergeConflict save(MergeConflict mergeConflict);

	@Transactional
	void delete(MergeConflict mergeConflict, String reason);
}
