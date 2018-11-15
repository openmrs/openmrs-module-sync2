package org.openmrs.module.sync2.api.dao;

import org.openmrs.module.sync2.api.model.MergeConflict;

public interface MergeConflictDao {

	MergeConflict getById(Integer id);

	MergeConflict getByUuid(String uuid);

	MergeConflict save(MergeConflict mergeConflict);
}
