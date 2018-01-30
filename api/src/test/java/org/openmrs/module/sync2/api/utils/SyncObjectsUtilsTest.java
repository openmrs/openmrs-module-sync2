package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.client.rest.resource.Patient;

import static org.openmrs.module.sync2.api.utils.SyncObjectsUtils.*;

public class SyncObjectsUtilsTest {

    private Class patientFhirClass = org.hl7.fhir.dstu3.model.Patient.class;
    private Class patientRestClass = Patient.class;
    private Class patientOpenmrsClass = org.openmrs.Patient.class;

    @Test
    public void getSyncedClass_shouldReturnFhirPatientClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.FHIR_CLIENT, SyncCategoryConstants.CATEGORY_PATIENT);
        Assert.assertEquals(patientFhirClass, fetched);
    }

    @Test
    public void getSyncedClass_shouldReturnRestPatientClass() {
        Class fetched = getSyncedClassByClient(SyncConstants.REST_CLIENT, SyncCategoryConstants.CATEGORY_PATIENT);
        Assert.assertEquals(patientRestClass, fetched);
    }

    @Test
    public void getFhirClass_shouldReturnRestPatientClass() {
        Assert.assertEquals(patientFhirClass, getFhirClass(SyncCategoryConstants.CATEGORY_PATIENT));
    }

    @Test
    public void getRestClass_shouldReturnFhirPatientClass() {
        Assert.assertEquals(patientRestClass, getRestClass(SyncCategoryConstants.CATEGORY_PATIENT));
    }

    @Test
    public void getOpenmrsClass_shouldReturnPatientClass() {
        Assert.assertEquals(patientOpenmrsClass, getOpenmrsClass(SyncCategoryConstants.CATEGORY_PATIENT));
    }
}