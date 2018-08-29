package org.openmrs.module.sync2.api.utils;

import static org.openmrs.module.sync2.api.utils.SyncObjectsUtils.getFhirClass;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.sync2.SyncCategoryConstants;

public class SyncObjectsUtilsTest {

    private Class patientFhirClass = org.hl7.fhir.dstu3.model.Patient.class;
    private Class patientOpenmrsClass = org.openmrs.Patient.class;
    private Class visitFhirClass = org.hl7.fhir.dstu3.model.Encounter.class;
    private Class visitOpenmrsClass = org.openmrs.Visit.class;
    private Class encounterFhirClass = org.hl7.fhir.dstu3.model.Encounter.class;
    private Class encounterOpenmrsClass = org.openmrs.Encounter.class;
    private Class obsFhirClass = org.hl7.fhir.dstu3.model.Observation.class;
    private Class obsOpenmrsClass = org.openmrs.Obs.class;

    
    @Test
    public void getFhirClass_shouldReturnRestPatientClass() {
        Assert.assertEquals(patientFhirClass, getFhirClass(SyncCategoryConstants.CATEGORY_PATIENT));
    }

    @Test
    public void getFhirClass_shouldReturnRestVisitClass() {
        Assert.assertEquals(visitFhirClass, getFhirClass(SyncCategoryConstants.CATEGORY_VISIT));
    }

    @Test
    public void getFhirClass_shouldReturnRestEncounterClass() {
        Assert.assertEquals(encounterFhirClass, getFhirClass(SyncCategoryConstants.CATEGORY_ENCOUNTER));
    }

    @Test
    public void getFhirClass_shouldReturnRestObsClass() {
        Assert.assertEquals(obsFhirClass, getFhirClass(SyncCategoryConstants.CATEGORY_OB));
    }
}