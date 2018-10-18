package org.openmrs.module.sync2.client.rest;

import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.sync2.client.RestResourceCreationUtil;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;

public class RESTClientHelperTest {

	private static final String TEST_URI = "https://test/uri";

	private static final String TEST_PATIENT_UUID = "test_uuid";

	@Test
	public void retrieveRequest() throws Exception {
		RequestEntity expected = new RequestEntity(HttpMethod.GET, URI.create(TEST_URI));

		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.retrieveRequest(TEST_URI));
	}

	@Test
	public void createRequest() throws Exception {
		Patient patient = new Patient();
		RestResource patientResource = RestResourceCreationUtil.createRestResourceFromOpenMRSData(patient);
		RequestEntity expected = new RequestEntity(patientResource, HttpMethod.POST, URI.create(TEST_URI));

		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.createRequest(TEST_URI, patient));
	}

	@Test
	public void deleteRequest() throws Exception {
		Patient patient = new Patient();
		patient.setUuid(TEST_PATIENT_UUID);
		RequestEntity expected = new RequestEntity(TEST_PATIENT_UUID, HttpMethod.DELETE,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.deleteRequest(TEST_URI, TEST_PATIENT_UUID));
	}

	@Test
	public void updateRequest() throws Exception {
		Patient patient = new Patient();
		patient.setUuid(TEST_PATIENT_UUID);
		RestResource patientResource = RestResourceCreationUtil.createRestResourceFromOpenMRSData(patient);
		RequestEntity expected = new RequestEntity(patientResource, HttpMethod.POST,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.updateRequest(TEST_URI, patient));
	}

	@Test
	public void resolveCategoryByCategory() {
		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(org.openmrs.module.sync2.client.rest.resource.Patient.class,
				restClientHelper.resolveCategoryByCategory(CATEGORY_PATIENT));
		assertEquals(org.openmrs.module.sync2.client.rest.resource.Visit.class,
				restClientHelper.resolveCategoryByCategory(CATEGORY_VISIT));
		assertEquals(org.openmrs.module.sync2.client.rest.resource.Encounter.class,
				restClientHelper.resolveCategoryByCategory(CATEGORY_ENCOUNTER));
	}

}
