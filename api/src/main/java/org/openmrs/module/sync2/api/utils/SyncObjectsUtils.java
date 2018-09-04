package org.openmrs.module.sync2.api.utils;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.client.rest.resource.Encounter;
import org.openmrs.module.sync2.client.rest.resource.Location;
import org.openmrs.module.sync2.client.rest.resource.Observation;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Privilege;
import org.openmrs.module.sync2.client.rest.resource.Visit;

import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_LOCATION;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_OB;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PRIVILEGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;

public class SyncObjectsUtils {
	public static Class getRestClass(String category) {
		switch (category) {
			case CATEGORY_PATIENT:
				return Patient.class;
			case CATEGORY_VISIT:
				return Visit.class;
			case CATEGORY_ENCOUNTER:
				return Encounter.class;
			case CATEGORY_OB:
				return Observation.class;
			case CATEGORY_LOCATION:
				return Location.class;
			case CATEGORY_PRIVILEGE:
				return Privilege.class;
			case CATEGORY_AUDIT_MESSAGE:
				return AuditMessage.class;
			default:
				throw new SyncException(String.format("Cannot resolve '%s' REST resources category", category));
		}
	}

	private SyncObjectsUtils() {}
}
