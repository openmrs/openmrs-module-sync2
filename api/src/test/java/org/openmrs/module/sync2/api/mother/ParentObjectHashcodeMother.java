package org.openmrs.module.sync2.api.mother;

import org.openmrs.module.sync2.api.model.ParentObjectHashcode;

public class ParentObjectHashcodeMother {

	public static final String OBJECT_UUID = "object-uuid";

	public static final String UUID = "uuid";

	private static final String HASHCODE = "hashcode";

	public static ParentObjectHashcode createInstance(int objectId) {
		return createInstance(objectId, UUID, HASHCODE);
	}

	private static ParentObjectHashcode createInstance(int objectId, String uuid, String hashcode) {
		ParentObjectHashcode parentObjectHashcode = new ParentObjectHashcode();
		parentObjectHashcode.setId(objectId);
		parentObjectHashcode.setObjectUuid(OBJECT_UUID);
		parentObjectHashcode.setHashcode(hashcode);
		parentObjectHashcode.setUuid(UUID);
		return parentObjectHashcode;
	}
}
