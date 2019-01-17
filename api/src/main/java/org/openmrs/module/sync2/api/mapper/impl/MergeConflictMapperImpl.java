package org.openmrs.module.sync2.api.mapper.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.utils.SimpleObjectSerializationUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.serialization.SerializationException;
import org.openmrs.serialization.SimpleXStreamSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sync.mergeConflictMapper")
public class MergeConflictMapperImpl implements MergeConflictMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(MergeConflictMapperImpl.class);

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
			try {
				mergeConflict.setOrgForeign(Context.getSerializationService().serialize(mergeConflictDto.getOrgForeign(),
						SimpleXStreamSerializer.class).getBytes());
				mergeConflict.setOrgLocal(Context.getSerializationService().serialize(mergeConflictDto.getOrgLocal(),
						SimpleXStreamSerializer.class).getBytes());
			} catch (SerializationException e) {
				LOGGER.error("Serialization error has occurred", e.getMessage());
			}
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
				local = Context.getSerializationService().deserialize(new String(mergeConflict.getOrgLocal()),
						objectsClass, SimpleXStreamSerializer.class);
				foreign = Context.getSerializationService().deserialize(new String(mergeConflict.getOrgForeign()),
						objectsClass, SimpleXStreamSerializer.class);
			}
			mergeConflictDto = new org.openmrs.module.fhir.api.merge.MergeConflict(objectsClass, local, foreign);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Not found {} class during mapping the MergeConflict.", mergeConflict.getFullClassName());
		} catch (SerializationException e) {
			LOGGER.error("Serialization error has occurred.", e.getMessage());
		}
		return mergeConflictDto;
	}

}
