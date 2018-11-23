package org.openmrs.module.sync2.api.mother;

import org.openmrs.module.webservices.rest.SimpleObject;

public abstract class SimpleObjectMother {

	private static final String VALUE_KEY = "VALUE_KEY";

	private static final String UUID_KEY = "uuid";

	private static final String AUDIT_INFO_KEY = "auditInfo";

	private static final String DATE_CHANGED_KEY = "dateChanged";

	private static final String TEST_VALUE = "Test value";

	public static SimpleObject createInstance(String uuid, String value) {
		SimpleObject result = new SimpleObject();
		result.add(UUID_KEY, uuid);
		result.add(VALUE_KEY, value);
		return result;
	}

	public static SimpleObject createInstanceWithDateChanged(String dateChange, boolean withAuditInfo) {
		SimpleObject simpleObject = new SimpleObject();
		SimpleObject auditInfo = new SimpleObject();
		if (withAuditInfo) {
			auditInfo.add(DATE_CHANGED_KEY, dateChange);
			simpleObject.add(AUDIT_INFO_KEY, auditInfo);
		}
		simpleObject.add(VALUE_KEY, TEST_VALUE);

		return simpleObject;
	}
}
