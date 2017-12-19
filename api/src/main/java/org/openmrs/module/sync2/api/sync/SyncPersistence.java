package org.openmrs.module.sync2.api.sync;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPersistence.class);

    private static final String FHIR = "fhir";
    private static final String REST = "rest";
    private static final String CATEGORY_PATIENT = "patient";
    private static final String ACTION_DELETED = "DELETED";
    private static final String ACTION_UPDATED = "UPDATED";
    private static final String VOIDING_REASON = "Voided by Sync 2";
    public void persistRetrievedData(Object retrievedObject, String action) {
        if (retrievedObject instanceof OpenmrsData) {
            persistRetrievedRestData(retrievedObject, action);
        } else if (retrievedObject instanceof DomainResource) {
            persistRetrievedFhirData(retrievedObject, action);
        }
    }

    public Object retrieveData(String client, String category, String uuid) {
        switch (client) {
            case FHIR:
                return retrieveFhirObject(category, uuid);
            case REST:
                return retrieveRestObject(category, uuid);
            default:
                LOGGER.warn(String.format("Unrecognized client %s, falling back to core OpenMrs object", client));
                return retrieveRestObject(category, uuid);
        }
    }

    private Object retrieveFhirObject(String category, String uuid) {
        switch (category) {
            case CATEGORY_PATIENT:
                return PatientStrategyUtil.getPatientStrategy().getPatient(uuid);
            default:
                LOGGER.warn(String.format("Unrecognized category %s", category));
                return null;
        }
    }

    private Object retrieveRestObject(String category, String uuid) {
        switch (category) {
            case CATEGORY_PATIENT:
                PatientService service = Context.getPatientService();
                return service.getPatientByUuid(uuid);
            default:
                LOGGER.warn(String.format("Unrecognized category %s", category));
                return null;
        }
    }

    private void persistRetrievedRestData(Object object, String action) {
        if (object instanceof Patient) {
            switch (action) {
                case ACTION_DELETED:
                    PatientService service = Context.getPatientService();
                    Patient patient = service.getPatientByUuid(((Patient) object).getUuid());
                    service.voidPatient(patient, VOIDING_REASON);
                    break;
                case ACTION_UPDATED:
                default:
                    Context.getPatientService().savePatient((Patient)object);
                    break;
            }

        }
    }

    private void persistRetrievedFhirData(Object object, String action) {
        if (object instanceof org.hl7.fhir.dstu3.model.Patient) {
            switch (action) {
                case ACTION_UPDATED:
                    PatientStrategyUtil.getPatientStrategy().updatePatient((org.hl7.fhir.dstu3.model.Patient)object,
                            ((org.hl7.fhir.dstu3.model.Patient)object).getId());
                    break;
                case ACTION_DELETED:
                    PatientStrategyUtil.getPatientStrategy().deletePatient(((org.hl7.fhir.dstu3.model.Patient)object).getId());
                    break;
                default:
                    PatientStrategyUtil.getPatientStrategy().createFHIRPatient((org.hl7.fhir.dstu3.model.Patient)object);
                    break;
            }
        }
    }
}
