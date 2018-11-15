package org.openmrs.module.sync2.api.mother;

import org.openmrs.module.sync2.api.model.MergeConflict;

public abstract class MergeConflictMother {

	public static final String CLASS_NAME = "org.openmrs.Class";

	public static final String MESSAGE = "Message";

	public static final String UUID = "uuid";

	public static MergeConflict createInstance(int mergeConflictId) {
		return createInstance(mergeConflictId, CLASS_NAME, MESSAGE);
	}

	public static MergeConflict createInstance(int id, String className, String message) {
		MergeConflict mergeConflict = new MergeConflict();
		mergeConflict.setId(id);
		mergeConflict.setFullClassName(className);
		mergeConflict.setMessage(message);
		mergeConflict.setOrgLocal(message.getBytes());
		mergeConflict.setOrgForeign(message.getBytes());
		mergeConflict.setUuid(UUID);
		return mergeConflict;
	}
}
