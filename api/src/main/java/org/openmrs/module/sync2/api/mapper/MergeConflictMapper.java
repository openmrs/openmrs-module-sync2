package org.openmrs.module.sync2.api.mapper;

import org.openmrs.module.sync2.api.model.MergeConflict;

public interface MergeConflictMapper {

	MergeConflict map(org.openmrs.module.fhir.api.merge.MergeConflict dtoMergeConflict);

	org.openmrs.module.fhir.api.merge.MergeConflict map(MergeConflict mergeConflict);
}
