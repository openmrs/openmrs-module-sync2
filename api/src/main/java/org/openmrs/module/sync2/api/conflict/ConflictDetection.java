package org.openmrs.module.sync2.api.conflict;

import org.openmrs.module.webservices.rest.SimpleObject;

public interface ConflictDetection {

	boolean detectConflict(SimpleObject currentObject, SimpleObject newObject);
}
