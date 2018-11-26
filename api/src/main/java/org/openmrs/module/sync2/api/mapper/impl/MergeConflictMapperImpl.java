package org.openmrs.module.sync2.api.mapper.impl;

import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.utils.SimpleObjectSerializationUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

@Component("sync.mergeConflictMapper")
public class MergeConflictMapperImpl implements MergeConflictMapper {

	public MergeConflict map(org.openmrs.module.fhir.api.merge.MergeConflict mergeConflictDto) {
		MergeConflict mergeConflict = new MergeConflict();
		mergeConflict.setMessage(mergeConflictDto.getMessage().getValue());
		Class objectsClass = mergeConflictDto.getClazz();
		mergeConflict.setFullClassName(objectsClass.getCanonicalName());
		if (objectsClass.isAssignableFrom(SimpleObject.class)) {
			mergeConflict.setOrgLocal(SimpleObjectSerializationUtils.serialize(
					(SimpleObject) mergeConflictDto.getOrgLocal()).getBytes());
			mergeConflict.setOrgForeign(SimpleObjectSerializationUtils.serialize(
					(SimpleObject) mergeConflictDto.getOrgForeign()).getBytes());
		} else {
			mergeConflict.setOrgLocal(SerializationUtils.serialize(mergeConflictDto.getOrgLocal()));
			mergeConflict.setOrgForeign(SerializationUtils.serialize(mergeConflictDto.getOrgForeign()));
		}
		return mergeConflict;
	}

	@Override
	public org.openmrs.module.fhir.api.merge.MergeConflict map(MergeConflict mergeConflict) {
		org.openmrs.module.fhir.api.merge.MergeConflict mergeConflictDto = null;
		try {
			Class objectsClass = Class.forName(mergeConflict.getFullClassName());
			Object local = null;
			Object foreign = null;
			if (objectsClass.isAssignableFrom(SimpleObject.class)) {
				local = SimpleObjectSerializationUtils.deserialize(new String(mergeConflict.getOrgLocal()));
				foreign = SimpleObjectSerializationUtils.deserialize(new String(mergeConflict.getOrgForeign()));
			} else {
				local = SerializationUtils.deserialize(mergeConflict.getOrgLocal());
				foreign = SerializationUtils.deserialize(mergeConflict.getOrgForeign());
			}
			mergeConflictDto = new org.openmrs.module.fhir.api.merge.MergeConflict(objectsClass, local, foreign);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return mergeConflictDto;
	}

}
