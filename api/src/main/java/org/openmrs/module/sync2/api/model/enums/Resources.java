package org.openmrs.module.sync2.api.model.enums;

import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ALLERGY;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_LOCATION;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_OBSERVATION;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PRIVILEGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;

public enum Resources {

    ALL(""),
    PATIENT(CATEGORY_PATIENT),
    VISIT(CATEGORY_VISIT),
    ENCOUNTER(CATEGORY_ENCOUNTER),
    OBSERVATION(CATEGORY_OBSERVATION),
    LOCATION(CATEGORY_LOCATION),
    PRIVILEGE(CATEGORY_PRIVILEGE),
    AUDIT_MESSAGE(CATEGORY_AUDIT_MESSAGE),
    ALLERGY(CATEGORY_ALLERGY);

    private final String name;

    Resources(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
