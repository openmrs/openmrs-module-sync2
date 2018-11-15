package org.openmrs.module.sync2.api.mapper.impl;

import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component("sync.mergeConflictMapper")
public class MergeConflictMapperImpl implements MergeConflictMapper {

	public MergeConflict map(org.openmrs.module.fhir.api.merge.MergeConflict mergeConflictDto) {
		MergeConflict mergeConflict = new MergeConflict();
		mergeConflict.setMessage(mergeConflictDto.getMessage().getValue());
		mergeConflict.setFullClassName(mergeConflictDto.getClazz().getCanonicalName());
		mergeConflict.setOrgLocal(SerializationUtils.serialize(mergeConflictDto.getOrgLocal()));
		mergeConflict.setOrgForeign(SerializationUtils.serialize(mergeConflictDto.getOrgForeign()));
		return mergeConflict;
	}

	@Override
	public org.openmrs.module.fhir.api.merge.MergeConflict map(MergeConflict mergeConflict) {
		org.openmrs.module.fhir.api.merge.MergeConflict mergeConflictDto = null;
		try {
			Object local = SerializationUtils.deserialize(mergeConflict.getOrgLocal());
			Object foreign = SerializationUtils.deserialize(mergeConflict.getOrgForeign());
			Class objectsClass = Class.forName(mergeConflict.getFullClassName());
			mergeConflictDto = new org.openmrs.module.fhir.api.merge.MergeConflict(objectsClass, local, foreign);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return mergeConflictDto;
	}

}
