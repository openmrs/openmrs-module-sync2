package org.openmrs.module.sync2.api.sync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.patient.GenericPatientStrategy;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategy;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncPersistence.class, PatientStrategyUtil.class, Context.class})
public class SyncPersistenceTest {

    private static final String FHIR = "fhir";
    private static final String REST = "rest";
    private static final String CATEGORY_PATIENT = "patient";
    private static final String UUID = "61b38324-e2fd-4feb-95b7-9e9a2a4400df";

    private static final String REST_METHOD = "persistRetrievedRestData";
    private static final String FHIR_METHOD = "persistRetrievedFhirData";
    private static final String ACTION = "CREATED";

    private SyncPersistence syncPersistence;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(PatientStrategyUtil.class);
        PowerMockito.mockStatic(Context.class);
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

    @Test
    public void retrieveData_shouldRetrieveFhirData() {
        // given
        GenericPatientStrategy patientStrategy = mock(PatientStrategy.class);
        org.hl7.fhir.dstu3.model.Patient patient = new org.hl7.fhir.dstu3.model.Patient();
        when(patientStrategy.getPatient(UUID)).thenReturn(patient);
        PowerMockito.when(PatientStrategyUtil.getPatientStrategy()).thenReturn(patientStrategy);

        // when
        Object object = syncPersistence.retrieveData(FHIR, CATEGORY_PATIENT, UUID);

        // then
        assertEquals(patient, object);
    }

    @Test
    public void retrieveData_shouldRetrieveRestData() {
        // given
        PatientService patientService = mock(PatientService.class);
        Patient patient = new Patient();
        when(patientService.getPatientByUuid(UUID)).thenReturn(patient);
        PowerMockito.when(Context.getPatientService()).thenReturn(patientService);

        // when
        Object object = syncPersistence.retrieveData(REST, CATEGORY_PATIENT, UUID);

        // then
        assertEquals(patient, object);
    }
}