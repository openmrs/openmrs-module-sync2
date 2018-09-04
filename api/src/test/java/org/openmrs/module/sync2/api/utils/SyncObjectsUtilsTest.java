package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.client.rest.resource.Encounter;
import org.openmrs.module.sync2.client.rest.resource.Observation;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Visit;

import static org.openmrs.module.sync2.api.utils.SyncObjectsUtils.getRestClass;

public class SyncObjectsUtilsTest {

    private Class patientRestClass = Patient.class;
    private Class visitRestClass = Visit.class;
    private Class encounterRestClass = Encounter.class;
    private Class obsRestClass = Observation.class;

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
}
