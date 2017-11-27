package org.openmrs.module.sync2.api.sync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncPersistence.class })
public class SyncPersistenceTest {

    private static final String REST_METHOD = "persistRetrievedRestData";
    private static final String FHIR_METHOD = "persistRetrievedFhirData";
    private static final String ACTION = "CREATED";

    private SyncPersistence syncPersistence;

    @Before
    public void setUp() throws Exception {
        syncPersistence = spy(new SyncPersistence());
        PowerMockito.doNothing().when(syncPersistence, REST_METHOD, anyObject(), anyString());
        PowerMockito.doNothing().when(syncPersistence, FHIR_METHOD, anyObject(), anyString());
    }

    @Test
    public void persistRetrievedData_shouldCallPersistRetrievedRestData() throws Exception {
        Object restResource = new Patient();
        syncPersistence.persistRetrievedData(restResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(REST_METHOD, restResource, ACTION);
    }

    @Test
    public void persistRetrievedData_shouldCallPersistRetrievedFhirData() throws Exception {
        org.hl7.fhir.dstu3.model.Patient fhirResource = new org.hl7.fhir.dstu3.model.Patient();
        syncPersistence.persistRetrievedData(fhirResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(FHIR_METHOD, fhirResource, ACTION);
    }
}