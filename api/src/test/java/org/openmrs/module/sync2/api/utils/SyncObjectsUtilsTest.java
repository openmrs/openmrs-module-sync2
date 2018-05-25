package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Visit;
import org.openmrs.module.sync2.client.rest.resource.Encounter;
import org.openmrs.module.sync2.client.rest.resource.Observation;

import static org.openmrs.module.sync2.api.utils.SyncObjectsUtils.*;

public class SyncObjectsUtilsTest {

    private Class patientFhirClass = org.hl7.fhir.dstu3.model.Patient.class;
    private Class patientRestClass = Patient.class;
    private Class patientOpenmrsClass = org.openmrs.Patient.class;
    private Class visitFhirClass = org.hl7.fhir.dstu3.model.Encounter.class;
    private Class visitRestClass = Visit.class;
    private Class visitOpenmrsClass = org.openmrs.Visit.class;
    private Class encounterFhirClass = org.hl7.fhir.dstu3.model.Encounter.class;
    private Class encounterRestClass = Encounter.class;
    private Class encounterOpenmrsClass = org.openmrs.Encounter.class;
    private Class obsFhirClass = org.hl7.fhir.dstu3.model.Observation.class;
    private Class obsRestClass = Observation.class;
    private Class obsOpenmrsClass = org.openmrs.Obs.class;

    @Test
    public void getSyncedClass_shouldReturnFhirPatientClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.FHIR_CLIENT, SyncCategoryConstants.CATEGORY_PATIENT);
        Assert.assertEquals(patientFhirClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnFhirVisitClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.FHIR_CLIENT, SyncCategoryConstants.CATEGORY_VISIT);
        Assert.assertEquals(visitFhirClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnFhirEncounterClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.FHIR_CLIENT, SyncCategoryConstants.CATEGORY_ENCOUNTER);
        Assert.assertEquals(encounterFhirClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnFhirObsClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.FHIR_CLIENT, SyncCategoryConstants.CATEGORY_OB);
        Assert.assertEquals(obsFhirClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnRestPatientClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.REST_CLIENT, SyncCategoryConstants.CATEGORY_PATIENT);
        Assert.assertEquals(patientRestClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnRestVisitClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.REST_CLIENT, SyncCategoryConstants.CATEGORY_VISIT);
        Assert.assertEquals(visitRestClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnRestEncounterClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.REST_CLIENT, SyncCategoryConstants.CATEGORY_ENCOUNTER);
        Assert.assertEquals(encounterRestClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnRestObsClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.REST_CLIENT, SyncCategoryConstants.CATEGORY_OB);
        Assert.assertEquals(obsRestClass, fetched);
    }
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

    @Test
    public void getRestClass_shouldReturnFhirPatientClass() {
        Assert.assertEquals(patientRestClass, getRestClass(SyncCategoryConstants.CATEGORY_PATIENT));
    }

    @Test
    public void getRestClass_shouldReturnFhirVisitClass() {
        Assert.assertEquals(visitRestClass, getRestClass(SyncCategoryConstants.CATEGORY_VISIT));
    }

    @Test
    public void getRestClass_shouldReturnFhirEncounterClass() {
        Assert.assertEquals(encounterRestClass, getRestClass(SyncCategoryConstants.CATEGORY_ENCOUNTER));
    }

    @Test
    public void getRestClass_shouldReturnFhirObsClass() {
        Assert.assertEquals(obsRestClass, getRestClass(SyncCategoryConstants.CATEGORY_OB));
    }

    @Test
    public void getOpenmrsClass_shouldReturnPatientClass() {
        Assert.assertEquals(patientOpenmrsClass, getOpenmrsClass(SyncCategoryConstants.CATEGORY_PATIENT));
    }

    @Test
    public void getOpenmrsClass_shouldReturnVisitClass() {
        Assert.assertEquals(visitOpenmrsClass, getOpenmrsClass(SyncCategoryConstants.CATEGORY_VISIT));
    }

    @Ignore
    @Test
    public void getOpenmrsClass_shouldReturnEncounterClass() {
        Assert.assertEquals(encounterOpenmrsClass, getOpenmrsClass(SyncCategoryConstants.CATEGORY_ENCOUNTER));
    }

    @Ignore
    @Test
    public void getOpenmrsClass_shouldReturnObsClass() {
        Assert.assertEquals(obsOpenmrsClass, getOpenmrsClass(SyncCategoryConstants.CATEGORY_OB));
    }
}