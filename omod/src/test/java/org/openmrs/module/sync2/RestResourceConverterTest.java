package org.openmrs.module.sync2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.module.sync2.client.rest.RestResourceConverter;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class RestResourceConverterTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	private RestResourceConverter converter;
	
	@Test
	public void convertObject_shouldConvertAnObservation() {
		SimpleObject obs = new SimpleObject();
		final String conceptUuid = "some-concept-uuid";
		SimpleObject concept = new SimpleObject();
		concept.add("uuid", conceptUuid);
		obs.add("uuid", "some-uuid");
		obs.add("concept", concept);
		converter.convertObject("/ws/rest/v1/obs", obs);
		assertEquals(conceptUuid, obs.get("concept"));
	}
	
}
