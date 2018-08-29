package org.openmrs.module.sync2.api.utils;

import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_LOCATION;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_OB;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PRIVILEGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;


public class SyncObjectsUtils {


    @SuppressWarnings("rawtypes")
    public static Class getFhirClass(String category) {
        // TODO: it should be refactored - placed in FHIR_CLIENT module and only used here
        switch (category) {
            case CATEGORY_PATIENT:
                return org.hl7.fhir.dstu3.model.Patient.class;
            case CATEGORY_VISIT:
                return org.hl7.fhir.dstu3.model.Encounter.class;
            case CATEGORY_ENCOUNTER:
                return org.hl7.fhir.dstu3.model.Encounter.class;
            case CATEGORY_OB:
                return org.hl7.fhir.dstu3.model.Observation.class;
            case CATEGORY_LOCATION:
                return org.hl7.fhir.dstu3.model.Location.class;
            default:
                throw new SyncException(String.format("Cannot resolve '%s' FHIR category", category));
        }
    }

    private SyncObjectsUtils() {}
}
