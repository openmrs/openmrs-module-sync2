package org.openmrs.module.sync2.api.sync;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.OpenmrsData;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;

public class SyncPersistence {

    public void persistRetrievedData(Object retrievedObject) {
        if (retrievedObject instanceof OpenmrsData) {
            persistRetrievedRestData(retrievedObject);
        } else if (retrievedObject instanceof DomainResource) {
            persistRetrievedFhirData(retrievedObject);
        }
    }

    private void persistRetrievedRestData(Object object) {
        if (object instanceof Patient) {
            Context.getPatientService().savePatient((Patient)object);
        }
    }

    private void persistRetrievedFhirData(Object object) {
        if (object instanceof org.hl7.fhir.dstu3.model.Patient) {
            PatientStrategyUtil.getPatientStrategy().createFHIRPatient((org.hl7.fhir.dstu3.model.Patient)object);
        }
    }
}
