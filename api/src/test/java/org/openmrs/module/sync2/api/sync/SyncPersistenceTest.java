package org.openmrs.module.sync2.api.sync;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.encounter.EncounterStrategy;
import org.openmrs.module.fhir.api.strategies.encounter.EncounterStrategyUtil;
import org.openmrs.module.fhir.api.strategies.encounter.GenericEncounterStrategy;
import org.openmrs.module.fhir.api.strategies.observation.GenericObservationStrategy;
import org.openmrs.module.fhir.api.strategies.observation.ObservationStrategy;
import org.openmrs.module.fhir.api.strategies.observation.ObservationStrategyUtil;
import org.openmrs.module.fhir.api.strategies.patient.GenericPatientStrategy;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategy;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;
import org.openmrs.module.fhir.api.strategies.visit.GenericVisitStrategy;
import org.openmrs.module.fhir.api.strategies.visit.VisitStrategy;
import org.openmrs.module.fhir.api.strategies.visit.VisitStrategyUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_OB;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncPersistence.class, PatientStrategyUtil.class, VisitStrategyUtil.class, Context.class})
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
        PowerMockito.mockStatic(VisitStrategyUtil.class);
        PowerMockito.mockStatic(Context.class);
        syncPersistence = spy(new SyncPersistence());
        PowerMockito.doNothing().when(syncPersistence, REST_METHOD, anyObject(), anyString());
        PowerMockito.doNothing().when(syncPersistence, FHIR_METHOD, anyObject(), anyString());
    }

    @Test
    public void persistRetrievedPatientData_shouldCallPersistRetrievedRestData() throws Exception {
        Object restResource = new Patient();
        syncPersistence.persistRetrievedData(restResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(REST_METHOD, restResource, ACTION);
    }

    @Test
    public void persistRetrievedVisitData_shouldCallPersistRetrievedRestData() throws Exception {
        Object restResource = new Visit();
        syncPersistence.persistRetrievedData(restResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(REST_METHOD, restResource, ACTION);
    }

    @Test
    public void persistRetrievedEncounterData_shouldCallPersistRetrievedRestData() throws Exception {
        Object restResource = new Encounter();
        syncPersistence.persistRetrievedData(restResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(REST_METHOD, restResource, ACTION);
    }
    
    @Test
    public void persistRetrievedObsData_shouldCallPersistRetrievedRestData() throws Exception {
        Object restResource = new Obs();
        syncPersistence.persistRetrievedData(restResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(REST_METHOD, restResource, ACTION);
    }

    @Test
    public void persistRetrievedPatientData_shouldCallPersistRetrievedFhirData() throws Exception {
        org.hl7.fhir.dstu3.model.Patient fhirResource = new org.hl7.fhir.dstu3.model.Patient();
        syncPersistence.persistRetrievedData(fhirResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(FHIR_METHOD, fhirResource, ACTION);
    }

    @Test
    public void persistRetrievedVisitData_shouldCallPersistRetrievedFhirData() throws Exception {
        org.hl7.fhir.dstu3.model.Encounter fhirResource = new org.hl7.fhir.dstu3.model.Encounter();
        syncPersistence.persistRetrievedData(fhirResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(FHIR_METHOD, fhirResource, ACTION);
    }

    @Test
    public void persistRetrievedEncounterData_shouldCallPersistRetrievedFhirData() throws Exception {
        org.hl7.fhir.dstu3.model.Encounter fhirResource = new org.hl7.fhir.dstu3.model.Encounter();
        syncPersistence.persistRetrievedData(fhirResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(FHIR_METHOD, fhirResource, ACTION);
    }

    @Test
    public void persistRetrievedObsData_shouldCallPersistRetrievedFhirData() throws Exception {
        org.hl7.fhir.dstu3.model.Observation fhirResource = new org.hl7.fhir.dstu3.model.Observation();
        syncPersistence.persistRetrievedData(fhirResource, ACTION);

        verifyPrivate(syncPersistence, times(1)).invoke(FHIR_METHOD, fhirResource, ACTION);
    }

    @Test
    public void retrievePatientData_shouldRetrieveFhirData() {
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
    public void retrieveVisitData_shouldRetrieveFhirData() {
        // given
        GenericVisitStrategy visitStrategy = mock(VisitStrategy.class);
        org.hl7.fhir.dstu3.model.Encounter visit = new org.hl7.fhir.dstu3.model.Encounter();
        when(visitStrategy.getVisit(UUID)).thenReturn(visit);
        PowerMockito.when(VisitStrategyUtil.getVisitStrategy()).thenReturn(visitStrategy);

        // when
        Object object = syncPersistence.retrieveData(FHIR, CATEGORY_VISIT, UUID);

        // then
        assertEquals(visit, object);
    }

    @Ignore
    @Test
    public void retrieveEncounterData_shouldRetrieveFhirData() {
        // given
        GenericEncounterStrategy encounterStrategy = mock(EncounterStrategy.class);
        org.hl7.fhir.dstu3.model.Encounter encounter = new org.hl7.fhir.dstu3.model.Encounter();
        when(encounterStrategy.getEncounter(UUID)).thenReturn(encounter);
        PowerMockito.when(EncounterStrategyUtil.getEncounterStrategy()).thenReturn(encounterStrategy);

        // when
        Object object = syncPersistence.retrieveData(FHIR, CATEGORY_ENCOUNTER, UUID);

        // then
        assertEquals(encounter, object);
    }

    @Ignore
    @Test
    public void retrieveObsData_shouldRetrieveFhirData() {
        // given
        GenericObservationStrategy obsStrategy = mock(ObservationStrategy.class);
        org.hl7.fhir.dstu3.model.Observation obs = new org.hl7.fhir.dstu3.model.Observation();
        when(obsStrategy.getObservation(UUID)).thenReturn(obs);
        PowerMockito.when(ObservationStrategyUtil.getObservationStrategy()).thenReturn(obsStrategy);

        // when
        Object object = syncPersistence.retrieveData(FHIR, CATEGORY_OB, UUID);

        // then
        assertEquals(obs, object);
    }


    @Test
    public void retrievePatientData_shouldRetrieveRestData() {
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

    @Test
    public void retrieveVisitData_shouldRetrieveRestData() {
        // given
        VisitService visitService = mock(VisitService.class);
        Visit visit = new Visit();
        when(visitService.getVisitByUuid(UUID)).thenReturn(visit);
        PowerMockito.when(Context.getVisitService()).thenReturn(visitService);

        // when
        Object object = syncPersistence.retrieveData(REST, CATEGORY_VISIT, UUID);

        // then
        assertEquals(visit, object);
    }

    @Ignore
    @Test
    public void retrieveEncounterData_shouldRetrieveRestData() {
        // given
        EncounterService encounterService = mock(EncounterService.class);
        Encounter encounter = new Encounter();
        when(encounterService.getEncounterByUuid(UUID)).thenReturn(encounter);
        PowerMockito.when(Context.getEncounterService()).thenReturn(encounterService);

        // when
        Object object = syncPersistence.retrieveData(REST, CATEGORY_VISIT, UUID);

        // then
        assertEquals(encounter, object);
    }

    @Ignore
    @Test
    public void retrieveObsData_shouldRetrieveRestData() {
        // given
        ObsService obsService = mock(ObsService.class);
        Obs ob = new Obs();
        when(obsService.getObsByUuid(UUID)).thenReturn(ob);
        PowerMockito.when(Context.getObsService()).thenReturn(obsService);

        // when
        Object object = syncPersistence.retrieveData(REST, CATEGORY_VISIT, UUID);

        // then
        assertEquals(ob, object);
    }
}