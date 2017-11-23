package org.openmrs.module.sync2.api.sync;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.client.rest.RestClient;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.powermock.api.mockito.PowerMockito.*;

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
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Test
    public void pullDataFromParent_shouldCallRestClient() throws Exception {
        Patient expectedPatient = createPatient();

        AdministrationService administrationServiceMock = mock(AdministrationService.class);

        mockStatic(Context.class);
        BDDMockito.given(Context.getAdministrationService()).willReturn(administrationServiceMock);
        BDDMockito.given(Context.getAdministrationService()).willReturn(administrationServiceMock);

        doReturn(USERNAME).when(administrationServiceMock).getGlobalProperty("sync2.user.login");
        doReturn(PASSWORD).when(administrationServiceMock).getGlobalProperty("sync2.user.password");

        SyncClient resourceManager = new SyncClient();
        Map<String, String> links = new HashMap<>();
        links.put(FHIR_CLIENT_KEY, FHIR_RESOURCE_LINK + PATIENT_UUID);
        links.put(REST_CLIENT_KEY, REST_RESOURCE_LINK + PATIENT_UUID);

        String url = PARENT_ADDRESS + REST_RESOURCE_LINK + PATIENT_UUID;

        RestClient restClientMock = mock(RestClient.class);
        doReturn(createPatient()).when(restClientMock).getObject(PATIENT_CATEGORY, url, USERNAME, PASSWORD);
        whenNew(RestClient.class).withNoArguments().thenReturn(restClientMock);

        PulledObject pulledObject = resourceManager.pullDataFromParent(PATIENT_CATEGORY, links, PARENT_ADDRESS);

        assertThat(pulledObject.getClientType().getSimpleName(), is(RestClient.class.getSimpleName()));
        assertThat(pulledObject.getResourceObject(), is(expectedPatient));

    }

    @Test
    public void pullDataFromParent_shouldCallFHIRClient() throws Exception {
        SyncClient resourceManager = new SyncClient();
        Map<String, String> links = new HashMap<>();
        links.put(FHIR_CLIENT_KEY, FHIR_RESOURCE_LINK + PATIENT_UUID);

        PulledObject pulledObject = resourceManager.pullDataFromParent(PATIENT_CATEGORY, links, PARENT_ADDRESS);

        assertThat(pulledObject, is(nullValue()));

    }

    private Patient createPatient() {
        Patient patient = new Patient();
        patient.setUuid(PATIENT_UUID);

        PatientIdentifier patientIdentifier = new org.openmrs.PatientIdentifier();
        patientIdentifier.setUuid("identifierId");
        patientIdentifier.setIdentifier("identifier");
        patientIdentifier.setPreferred(true);
        patientIdentifier.setVoided(false);

        Set<PatientIdentifier> patientIdentifierList = new HashSet<>();
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

        patient.setNames(Collections.singleton(personName));

        patient.setVoided(false);
        patient.setDeathdateEstimated(false);
        patient.setBirthtime(new Date());

        return patient;
    }
}
