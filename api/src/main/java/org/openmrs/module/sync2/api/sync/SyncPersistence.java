package org.openmrs.module.sync2.api.sync;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;

public class SyncPersistence {

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
