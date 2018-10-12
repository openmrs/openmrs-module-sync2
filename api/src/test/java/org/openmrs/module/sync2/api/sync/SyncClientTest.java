package org.openmrs.module.sync2.api.sync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.FHIRClientHelper;
import org.openmrs.module.sync2.client.ClientHelperFactory;
import org.openmrs.module.sync2.client.rest.RESTClientHelper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.openmrs.module.sync2.SyncConstants.PARENT_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_USERNAME_PROPERTY;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncClient.class, Context.class })
public class SyncClientTest {

    private static final String PATIENT_UUID = "patientUuid";
    private static final String VISIT_UUID = "visitUuid";
    private static final String ENCOUNTER_UUID = "encounterUuid";
    private static final String OBS_UUID = "obsUuid";
    private static final String FHIR_CLIENT_KEY = "fhir";
    private static final String REST_CLIENT_KEY = "rest";
    private static final String FHIR_RESOURCE_LINK = "openmrs/ws/fhir/Patient/";
    private static final String REST_RESOURCE_LINK = "openmrs/ws/rest/v1/patient/";
    private static final String PATIENT_CATEGORY = "patient";
    private static final String VISIT_CATEGORY = "visit";
    private static final String ENCOUNTER_CATEGORY = "encounter";
    private static final String OB_CATEGORY = "ob";
    private static final String PARENT_ADDRESS = "http://localhost:8080/";
    private static final String PARENT_FEED_LOCATION = "http://localhost:8080/openmrs";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    
    private static final String REST_FULL_RESOURCE_URL = PARENT_ADDRESS + REST_RESOURCE_LINK + PATIENT_UUID;
    private static final String FHIR_FULL_RESOURCE_URL = PARENT_ADDRESS + FHIR_RESOURCE_LINK + PATIENT_UUID;
    
    private AdministrationService administrationServiceMock;
    private Patient expectedPatient;
    private Visit expectedVisit;
    private Encounter expectedEncounter;
    private Obs expectedOb;
    private Map<String, String> links;

    @Before
    public void setUp() throws Exception {
        expectedPatient = createPatient();
        expectedVisit = createVisit();
        administrationServiceMock = mock(AdministrationService.class);

        mockStatic(Context.class);
        BDDMockito.given(Context.getAdministrationService()).willReturn(administrationServiceMock);
        BDDMockito.given(Context.getAdministrationService()).willReturn(administrationServiceMock);

        doReturn(USERNAME).when(administrationServiceMock).getGlobalProperty(PARENT_USERNAME_PROPERTY);
        doReturn(PASSWORD).when(administrationServiceMock).getGlobalProperty(PARENT_PASSWORD_PROPERTY);
        doReturn(PARENT_FEED_LOCATION).when(administrationServiceMock).getGlobalProperty("sync2.general.parentFeedLocation");

        links = new HashMap<>();
        links.put(FHIR_CLIENT_KEY, FHIR_RESOURCE_LINK + PATIENT_UUID);
        links.put(REST_CLIENT_KEY, REST_RESOURCE_LINK + PATIENT_UUID);

        RESTClientHelper restClientHelper = new RESTClientHelper();
        FHIRClientHelper fhirClientHelper = new FHIRClientHelper();

        ClientHelperFactory clientHelperFactory = mock(ClientHelperFactory.class);
        doReturn(restClientHelper).when(clientHelperFactory).createClient(REST_CLIENT_KEY);
        doReturn(fhirClientHelper).when(clientHelperFactory).createClient(FHIR_CLIENT_KEY);
        whenNew(ClientHelperFactory.class).withNoArguments().thenReturn(clientHelperFactory);

        SyncClient syncClient =  PowerMockito.spy(new SyncClient());
        PowerMockito.doReturn(createPatient())
                .when(syncClient, "retrieveObject", PATIENT_CATEGORY, REST_FULL_RESOURCE_URL, restClientHelper);
        PowerMockito.doReturn(createVisit())
                .when(syncClient, "retrieveObject", VISIT_CATEGORY, REST_FULL_RESOURCE_URL, restClientHelper);
        PowerMockito.doReturn(createPatient())
                .when(syncClient, "retrieveObject", PATIENT_CATEGORY, FHIR_FULL_RESOURCE_URL, fhirClientHelper);
        PowerMockito.doReturn(createVisit())
                .when(syncClient, "retrieveObject", VISIT_CATEGORY, FHIR_FULL_RESOURCE_URL, fhirClientHelper);
        whenNew(SyncClient.class).withNoArguments().thenReturn(syncClient);
    }
    
    @Test
    public void pullDataFromParent_shouldCallRestClient() {
        SyncClient resourceManager = new SyncClient();

        Object pulledObject = resourceManager.pullData(PATIENT_CATEGORY, REST_CLIENT_KEY,
                REST_FULL_RESOURCE_URL, PARENT);

        assertThat(pulledObject, is(expectedPatient));
    }

    @Test
    public void pullDataFromParent_shouldCallFHIRClient() {
        SyncClient resourceManager = new SyncClient();

        Object pulledObject = resourceManager.pullData(PATIENT_CATEGORY, FHIR_CLIENT_KEY,
                FHIR_FULL_RESOURCE_URL, PARENT);

        assertThat(pulledObject, is(expectedPatient));
    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setUuid(PATIENT_UUID);

        PatientIdentifier patientIdentifier = new org.openmrs.PatientIdentifier();
        patientIdentifier.setUuid("identifierId");
        patientIdentifier.setIdentifier("identifier");
        patientIdentifier.setPreferred(true);
        patientIdentifier.setVoided(false);

        Set<PatientIdentifier> patientIdentifierList = new TreeSet<>();
            patientIdentifierList.add(patientIdentifier);

        patient.setIdentifiers(patientIdentifierList);
        patient.setGender("M");
        patient.setBirthdate(new Date());
        patient.setDead(false);
        patient.setDeathDate(null);
        patient.setCauseOfDeath(null);

        PersonName personName = new PersonName();
        personName.setUuid("nameUuid");
        personName.setGivenName("John");
        personName.setMiddleName(null);
        personName.setFamilyName("Smith");
        personName.setFamilyName2(null);
        personName.setVoided(false);

        Set<PersonName> personNameSet = new TreeSet<>();
        personNameSet.add(personName);
        patient.setNames(personNameSet);

        patient.setVoided(false);
        patient.setDeathdateEstimated(false);
        patient.setBirthtime(new Date());

        return patient;
    }

    private Visit createVisit() {
        Visit visit =  new Visit();
        visit.setUuid(VISIT_UUID);
        visit.setDateChanged(new Date());
        visit.setDateCreated(new Date());
        Encounter encounter = new Encounter();
        encounter.setUuid(ENCOUNTER_UUID);
        encounter.setDateChanged(new Date());
        encounter.setDateCreated(new Date());
        encounter.setEncounterDatetime(new Date());
        visit.addEncounter(encounter);
        visit.setIndication(null);
        visit.setLocation(null);
        visit.setStartDatetime(new Date());
        visit.setStopDatetime(new Date());
        visit.setVoided(false);
        return visit;
    }

    private Encounter createEncounter() {
        Encounter encounter = new Encounter();
        encounter.setUuid(ENCOUNTER_UUID);
        encounter.setDateChanged(new Date());
        encounter.setDateCreated(new Date());
        encounter.setEncounterDatetime(new Date());
        encounter.setForm(null);
        encounter.setLocation(null);
        Obs obs = new org.openmrs.Obs();
        obs.setUuid(OBS_UUID);
        obs.setAccessionNumber("");
        obs.setComment("");
        obs.setDateCreated(new Date());
        obs.setDateChanged(new Date());
        Set<Obs> observations = new TreeSet<>();
        observations.add(obs);
        encounter.setObs(observations);
        Order order = new org.openmrs.Order();
        order.setUuid("orderId");
        order.setDateCreated(new Date());
        order.setDateActivated(new Date());
        order.setDateChanged(new Date());
        Set<Order> orders = new TreeSet<>();
        orders.add(order);
        encounter.setOrders(orders);
        encounter.setVoided(true);
        return encounter;
    }

    private Obs createOb() {
        Obs obs = new org.openmrs.Obs();
        obs.setUuid(OBS_UUID);
        obs.setAccessionNumber("");
        obs.setComment("");
        obs.setDateCreated(new Date());
        obs.setDateChanged(new Date());
        obs.setObsGroup(null);
        obs.setLocation(null);
        obs.setObsDatetime(new Date());
        obs.setValueText("");
        obs.setValueTime(new Date());
        obs.setVoided(false);
        return obs;
    }
}
