package org.openmrs.module.sync2.api.sync;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;
import org.openmrs.module.sync2.client.rest.resource.RestResource;

public class SyncPersistence {

    public void persistRetrievedData(Object retrievedObject) {
        if (retrievedObject instanceof RestResource) {
            persistRetrievedRestData(retrievedObject);
        } else if (retrievedObject instanceof DomainResource) {
            persistRetrievedFhirData(retrievedObject);
        }
    }

    private void persistRetrievedRestData(Object object) {
        if (object instanceof org.openmrs.module.sync2.client.rest.resource.Patient) {
            Patient patient = (Patient)(((org.openmrs.module.sync2.client.rest.resource.Patient)object).getOpenMrsObject());
            Context.getPatientService().savePatient(patient);
        }
    }

    private void persistRetrievedFhirData(Object object) {
        if (object instanceof org.hl7.fhir.dstu3.model.Patient) {
            PatientStrategyUtil.getPatientStrategy().createFHIRPatient((org.hl7.fhir.dstu3.model.Patient)object);
        }
    }
}
