package org.openmrs.module.sync2.api.conflict.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.fhir.api.merge.MergeBehaviour;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sync2.conflictMergeBehaviour")
public class ConflictMergeBehaviourImpl implements MergeBehaviour<SimpleObject> {

	private static final String UUID_KEY = "uuid";

	@Autowired
	private ParentObjectHashcodeService parentObjectHashcodeService;

	@Override
	public MergeResult<SimpleObject> resolveDiff(Class<? extends SimpleObject> clazz, SimpleObject local,
			SimpleObject foreign) {
		return revolveConflict(clazz, local, foreign);
	}

	private MergeResult<SimpleObject> revolveConflict(Class<? extends SimpleObject> clazz, SimpleObject currentObject,
			SimpleObject newObject) {
		MergeResult result = new MergeConflict(clazz, currentObject, newObject);
		String localHashCode = SyncHashcodeUtils.getHashcode(currentObject);
		String foreignHashCode = SyncHashcodeUtils.getHashcode(newObject);
		String objectUuid = currentObject.get(UUID_KEY);
		ParentObjectHashcode previousHashCode = parentObjectHashcodeService.getByObjectUuid(objectUuid);

		if (StringUtils.isNotBlank(localHashCode) && StringUtils.isNotBlank(foreignHashCode)) {
			if (previousHashCode == null) {
				result = new MergeSuccess(clazz, currentObject, newObject, newObject, false, true);
			} else {
				boolean localEquals = previousHashCode.getHashcode().equalsIgnoreCase(localHashCode);
				boolean foreignEquals = previousHashCode.getHashcode().equalsIgnoreCase(foreignHashCode);
				if (localEquals && !foreignEquals) {
					result = new MergeSuccess(clazz, currentObject, newObject, newObject, false, true);
				} else if (!localEquals && foreignEquals) {
					result = new MergeSuccess(clazz, currentObject, newObject, currentObject, true, false);
				}
			}
		}
		return result;
	}
}
