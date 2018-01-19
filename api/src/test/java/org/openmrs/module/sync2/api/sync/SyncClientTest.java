package org.openmrs.module.sync2.api.sync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.FHIRClient;
import org.openmrs.module.sync2.client.ClientFactory;
import org.openmrs.module.sync2.client.rest.RestClient;
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
    private static final String FHIR_CLIENT_KEY = "fhir";
    private static final String REST_CLIENT_KEY = "rest";
    private static final String FHIR_RESOURCE_LINK = "openmrs/ws/fhir/Patient/";
    private static final String REST_RESOURCE_LINK = "openmrs/ws/rest/v1/patient/";
    private static final String PATIENT_CATEGORY = "patient";
    private static final String PARENT_ADDRESS = "http://localhost:8080/";
    private static final String PARENT_FEED_LOCATION = "http://localhost:8080/openmrs";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    
    private static final String REST_FULL_RESOURCE_URL = PARENT_ADDRESS + REST_RESOURCE_LINK + PATIENT_UUID;
    private static final String FHIR_FULL_RESOURCE_URL = PARENT_ADDRESS + FHIR_RESOURCE_LINK + PATIENT_UUID;
    
    private AdministrationService administrationServiceMock;
    private Patient expectedPatient;
    private Map<String, String> links;

    @Before
    public void setUp() throws Exception {
        expectedPatient = createPatient();

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


        RestClient restClientMock = mock(RestClient.class);
        doReturn(createPatient()).when(restClientMock).retrieveObject(PATIENT_CATEGORY, REST_FULL_RESOURCE_URL, USERNAME, PASSWORD);

        FHIRClient fhirClientMock = mock(FHIRClient.class);
        doReturn(createPatient()).when(fhirClientMock).retrieveObject(PATIENT_CATEGORY, FHIR_FULL_RESOURCE_URL, USERNAME, PASSWORD);

        ClientFactory clientFactory = mock(ClientFactory.class);
        doReturn(restClientMock).when(clientFactory).createClient(REST_CLIENT_KEY);
        doReturn(fhirClientMock).when(clientFactory).createClient(FHIR_CLIENT_KEY);
        whenNew(ClientFactory.class).withNoArguments().thenReturn(clientFactory);
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
}
