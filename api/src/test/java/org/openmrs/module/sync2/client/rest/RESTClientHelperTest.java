package org.openmrs.module.sync2.client.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.client.rest.impl.RestResourceConverterImpl;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.util.SimpleObjectConverter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_PATIENT;
import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_VISIT;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RESTClientHelper.class, Context.class})
public class RESTClientHelperTest {

	private static final String TEST_URI = "https://test/uri";

	private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private static final String TEST_PATIENT_UUID = "test_uuid";

	@Autowired
	private RESTClientHelper restClientHelper;

	@Autowired
	private SimpleObjectConverter simpleObjectConverter;

	@Before
	public void setUp() throws Exception {
		restClientHelper = PowerMockito.spy(new RESTClientHelper());
		PowerMockito.doReturn(new RestResourceConverterImpl()).when(restClientHelper, "getRestResourceConverter");
	}

	@Test
	public void retrieveRequest() throws Exception {
		RequestEntity expected = new RequestEntity(HttpMethod.GET, URI.create(TEST_URI));

		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.retrieveRequest(TEST_URI));
	}

	@Test
	public void createRequest() throws Exception {
		Patient patient = new Patient();
		SimpleObject simpleObject = getSimpleObject(patient);
		RequestEntity expected = new RequestEntity(simpleObject, HttpMethod.POST, URI.create(TEST_URI));

		assertEquals(expected, restClientHelper.createRequest(TEST_URI, simpleObject));
	}

	@Test
	public void deleteRequest() throws Exception {
		RequestEntity expected = new RequestEntity(TEST_PATIENT_UUID, HttpMethod.DELETE,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));
		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(expected, restClientHelper.deleteRequest(TEST_URI, TEST_PATIENT_UUID));
	}

	@Test
	public void updateRequest() throws Exception {
		Patient patient = new Patient();
		patient.setUuid(TEST_PATIENT_UUID);
		SimpleObject simpleObject = getSimpleObject(patient);
		RequestEntity expected = new RequestEntity(simpleObject, HttpMethod.POST,
				URI.create(TEST_URI + "/" + TEST_PATIENT_UUID));

		assertEquals(expected, restClientHelper.updateRequest(TEST_URI, simpleObject));
	}

	@Test
	public void resolveCategoryByCategory() {
		RESTClientHelper restClientHelper = new RESTClientHelper();
		assertEquals(SimpleObject.class, restClientHelper.resolveCategoryByCategory(CATEGORY_PATIENT));
		assertEquals(SimpleObject.class, restClientHelper.resolveCategoryByCategory(CATEGORY_VISIT));
		assertEquals(AuditMessage.class, restClientHelper.resolveCategoryByCategory(CATEGORY_AUDIT_MESSAGE));
	}

	private SimpleObject getSimpleObject(Object object) throws IOException {
		String json = getJson(object);
		return SimpleObject.parseJson(json);
	}

	private String getJson(Object object) {
		Gson defaultJsonParser = createDefaultGson();
		return defaultJsonParser.toJson(object);
	}

	private Gson createDefaultGson() {
		Gson gson = new GsonBuilder()
				.setDateFormat(ISO_8601)
				.create();
		TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
		TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();
		return new GsonBuilder()
				.setDateFormat(ISO_8601)
				.registerTypeAdapter(Date.class, safeDateTypeAdapter)
				.create();
	}

}
