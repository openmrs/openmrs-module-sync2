package org.openmrs.module.sync2.client.rest;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.ClientHttpEntity;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.sync2.client.rest.impl.RestResourceConverterImpl;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.createDefaultGson;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RESTClientHelper.class, Context.class, ContextUtils.class })
public class RESTClientHelperTest {

	private static final String TEST_URI = "https://test/uri";

	private static final String TEST_PATIENT_UUID = "test_uuid";

	private RESTClientHelper restClientHelper;

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(ContextUtils.class);
		when(ContextUtils.getConversionService()).thenReturn(new DefaultConversionService());
		restClientHelper = PowerMockito.spy(new RESTClientHelper());
		PowerMockito.doReturn(new RestResourceConverterImpl()).when(restClientHelper, "getRestResourceConverter");
	}

	@Test
	public void retrieveRequest() throws Exception {
		ClientHttpEntity expected = new ClientHttpEntity(HttpMethod.GET, URI.create(TEST_URI));

		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.retrieveRequest(TEST_URI));
	}

	@Test
	public void createRequest() throws Exception {
		Patient patient = new Patient();
		SimpleObject simpleObject = getSimpleObject(patient);
		String json = (new SimpleObjectMessageConverter()).convertToJson(simpleObject);
		ClientHttpEntity expected = new ClientHttpEntity<String>(json, HttpMethod.POST, URI.create(TEST_URI));

		assertEquals(expected, restClientHelper.createRequest(TEST_URI, simpleObject));
	}

	@Test
	public void deleteRequest() throws Exception {
		ClientHttpEntity expected = new ClientHttpEntity<String>(TEST_PATIENT_UUID, HttpMethod.DELETE,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));
		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.deleteRequest(TEST_URI, TEST_PATIENT_UUID));
	}

	@Test
	public void updateRequest() throws Exception {
		Patient patient = new Patient();
		patient.setUuid(TEST_PATIENT_UUID);
		SimpleObject simpleObject = getSimpleObject(patient);
		String json = (new SimpleObjectMessageConverter()).convertToJson(simpleObject);
		ClientHttpEntity expected = new ClientHttpEntity<String>(json, HttpMethod.POST,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		assertEquals(expected, restClientHelper.updateRequest(TEST_URI, simpleObject));
	}

	@Test
	public void resolveClassByCategory() {
		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(SimpleObject.class, restClientHelper.resolveClassByCategory(CATEGORY_PATIENT));
		assertEquals(SimpleObject.class, restClientHelper.resolveClassByCategory(CATEGORY_VISIT));
		assertEquals(AuditMessage.class, restClientHelper.resolveClassByCategory(CATEGORY_AUDIT_MESSAGE));
	}

	private SimpleObject getSimpleObject(Object object) throws IOException {
		String json = getJson(object);
		return SimpleObject.parseJson(json);
	}

	private String getJson(Object object) {
		Gson defaultJsonParser = createDefaultGson();
		return defaultJsonParser.toJson(object);
	}

}
