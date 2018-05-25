package org.openmrs.module.sync2.api.utils;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.client.rest.resource.Location;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Privilege;
import org.openmrs.module.sync2.client.rest.resource.Visit;
import org.openmrs.module.sync2.client.rest.resource.Encounter;
import org.openmrs.module.sync2.client.rest.resource.Observation;

import static org.openmrs.module.sync2.SyncCategoryConstants.*;
import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;


public class SyncObjectsUtils {

    public static Class getSyncedClassByClient(String client, String category) {
        switch (client) {
            case FHIR_CLIENT:
                return getFhirClass(category);
            case REST_CLIENT:
                return getRestClass(category);
            default:
                throw new SyncException(String.format("'%s' resource is not supported", category));
        }
    }

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

    public static Class getOpenmrsClass(String category) {
        switch (category) {
            case CATEGORY_PATIENT:
                return org.openmrs.Patient.class;
            case CATEGORY_VISIT:
                return org.openmrs.Visit.class;
            case CATEGORY_ENCOUNTER:
                return org.openmrs.Encounter.class;
            case CATEGORY_OB:
                return org.openmrs.Obs.class;
            case CATEGORY_LOCATION:
                return org.openmrs.Location.class;
            case CATEGORY_PRIVILEGE:
                return org.openmrs.Privilege.class;
            case CATEGORY_AUDIT_MESSAGE:
                return AuditMessage.class;
            default:
                throw new SyncException(String.format("Cannot resolve '%s' OpenmrsObject category", category));
        }
    }

    private SyncObjectsUtils() {}
}
