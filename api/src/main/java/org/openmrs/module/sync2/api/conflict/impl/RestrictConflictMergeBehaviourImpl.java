package org.openmrs.module.sync2.api.conflict.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.fhir.api.merge.MergeBehaviour;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sync2.conflictMergeBehaviour")
public class RestrictConflictMergeBehaviourImpl implements MergeBehaviour<SyncObject> {

	private static final String UUID_KEY = "uuid";

	@Autowired
	private ParentObjectHashcodeService parentObjectHashcodeService;

	@Override
	public MergeResult<SyncObject> resolveDiff(Class<? extends SyncObject> clazz, SyncObject local, SyncObject foreign) {
		return revolveConflict(local, foreign);
	}

	private MergeResult<SyncObject> revolveConflict(SyncObject currentObject, SyncObject newObject) {
		Class storedClass = SimpleObject.class;
		MergeResult<SyncObject> result = new MergeConflict<>(storedClass, currentObject.getSimpleObject(), newObject.getSimpleObject());
		String localHashCode = SyncHashcodeUtils.getHashcode(currentObject.getSimpleObject());
		String foreignHashCode = SyncHashcodeUtils.getHashcode(newObject.getSimpleObject());
		String objectUuid = (currentObject.getSimpleObject() != null ) ? currentObject.getSimpleObject().get(UUID_KEY) : null;
		ParentObjectHashcode previousHashCode = parentObjectHashcodeService.getByObjectUuid(objectUuid);

		if (StringUtils.isNotBlank(localHashCode) && StringUtils.isNotBlank(foreignHashCode)) {
			if (previousHashCode == null) {
				result = new MergeSuccess<>(storedClass, currentObject.getSimpleObject(), newObject.getSimpleObject(),
						newObject.getBaseObject(), false, true);
			} else {
				boolean localEquals = previousHashCode.getHashcode().equalsIgnoreCase(localHashCode);
				boolean foreignEquals = previousHashCode.getHashcode().equalsIgnoreCase(foreignHashCode);
				if (localEquals && !foreignEquals) {
					result = new MergeSuccess<>(storedClass, currentObject.getSimpleObject(), newObject.getSimpleObject(),
							newObject.getBaseObject(), false, true);
				} else if (!localEquals && foreignEquals) {
					result = new MergeSuccess<>(storedClass, currentObject.getSimpleObject(), newObject.getSimpleObject(),
							currentObject.getBaseObject(), true, false);
				}
			}
		}
		return result;
	}
}
