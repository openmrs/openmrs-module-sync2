package org.openmrs.module.sync2.api.conflict.impl;

import org.openmrs.module.sync2.api.conflict.ConflictDetection;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sync2.conflictDetection")
public class ConflictDetectionImpl implements ConflictDetection {

	private static final String UUID_KEY = "uuid";

	@Autowired
	private ParentObjectHashcodeService parentObjectHashcodeService;

	@Override
	public boolean detectConflict(SimpleObject currentObject, SimpleObject newObject) {
		boolean conflictDetect = false;
		String currentHashCode = SyncHashcodeUtils.getHashcode(currentObject);
		String newHashCode = SyncHashcodeUtils.getHashcode(newObject);
		String objectUuid = currentObject.get(UUID_KEY);
		ParentObjectHashcode previousHashCode = parentObjectHashcodeService.getByObjectUuid(objectUuid);
		if (previousHashCode != null
				&& (!previousHashCode.getHashcode().equalsIgnoreCase(newHashCode)
					|| !previousHashCode.getHashcode().equalsIgnoreCase(currentHashCode))) {
			conflictDetect = true;
		}

		return conflictDetect;
	}

}
