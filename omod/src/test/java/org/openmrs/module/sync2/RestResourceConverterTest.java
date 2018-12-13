package org.openmrs.module.sync2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.client.rest.RestResourceConverter;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class RestResourceConverterTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String WS_REST_V1 = "/ws/rest/v1/";
	
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
		converter.convertObject(WS_REST_V1 + "obs", obs);
		assertEquals(conceptUuid, obs.get("concept"));
	}
	
	@Test
	public void convertObject_shouldConvertPersonAttributesToUuids() throws Exception {
		final String conceptUuid = "32d3611a-6699-4d52-823f-b4b788bac3e3";
		SimpleObject concept = new SimpleObject();
		concept.add("uuid", conceptUuid);
		SimpleObject attributeWithValueAsConcept = new SimpleObject();
		attributeWithValueAsConcept.add("attributeType", "a0f5521c-dbbd-4c10-81b2-1b7ab18330df");
		attributeWithValueAsConcept.add("value", concept);
		
		final String valueProviderUuid = "c2299800-cca9-11e0-9572-0800200c9a66";
		PersonAttributeType personalDocAttributeType = new PersonAttributeType();
		personalDocAttributeType.setName("Personal Doctor");
		personalDocAttributeType.setFormat(Provider.class.getName());
		Context.getPersonService().savePersonAttributeType(personalDocAttributeType);
		
		SimpleObject provider = new SimpleObject();
		provider.add("uuid", valueProviderUuid);
		SimpleObject attributeWithValueAsProvider = new SimpleObject();
		//Should work for an attribute type set as a map
		SimpleObject personalDocAttributeTypeSo = new SimpleObject();
		personalDocAttributeTypeSo.add("uuid", personalDocAttributeType.getUuid());
		attributeWithValueAsProvider.add("attributeType", personalDocAttributeTypeSo);
		attributeWithValueAsProvider.add("value", provider);
		
		SimpleObject person = new SimpleObject();
		List<SimpleObject> attributes = new ArrayList<>();
		attributes.add(attributeWithValueAsConcept);
		attributes.add(attributeWithValueAsProvider);
		person.add("attributes", attributes);
		
		converter.convertObject(WS_REST_V1 + "person", person);
		
		List<Object> uuids = new ArrayList<>();
		//Find attributes with values set to uuids
		((List<Map>) person.get("attributes")).stream().forEach((Map map) -> {
			if (map.get("value") instanceof String) {
				uuids.add(map.get("value"));
			}
		});
		
		//Provider isn't Attributable so it should not have been converted to a uuid
		assertEquals(1, uuids.size());
		assertEquals(conceptUuid, uuids.get(0));
	}
	
}
