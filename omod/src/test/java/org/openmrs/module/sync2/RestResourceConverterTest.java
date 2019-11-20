package org.openmrs.module.sync2;

import org.junit.Test;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.client.rest.RestResourceConverter;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RestResourceConverterTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String WS_REST_V1 = "/ws/rest/v1/";
	private static final String CONCEPT_UUID = "some-concept-uuid";
	@Autowired
	private RestResourceConverter converter;


	@Test
	public void convertObject_shouldConvertAnObservation() {
		SimpleObject obs = createObsSimpleObject();
		converter.convertObject(WS_REST_V1 + "obs", obs);
		assertEquals(CONCEPT_UUID, obs.get("concept"));
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
		personalDocAttributeType.setDescription("Personal Doctor");
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

		for (Map map : (List<Map>) person.get("attributes")) {
			if (map.get("value") instanceof String) {
				uuids.add(map.get("value"));
			}
		}

		//Provider isn't Attributable so it should not have been converted to a uuid
		assertEquals(1, uuids.size());
		assertEquals(conceptUuid, uuids.get(0));
	}

	@Test
	public void convertObjectShouldConvertEncounterToSuitableRepresentationForRestPost() {
		SimpleObject encounter = new SimpleObject();
		final String ENCOUNTER_UUID = "some-encounter-uuid-who-gives-a-damn?";
		encounter.add("uuid", ENCOUNTER_UUID);
		SimpleObject obs = createObsSimpleObject();
		obs.add("encounter", encounter);

		converter.convertObject(WS_REST_V1 + "obs", obs);

		assertEquals(ENCOUNTER_UUID, obs.get("encounter"));
	}

	@Test
	public void convertObjectShouldNotAddEncounterIfNotIncludedYet() {
		SimpleObject obs = createObsSimpleObject();
		converter.convertObject(WS_REST_V1 + "obs", obs);
		assertFalse(obs.containsKey("encounter"));
	}

	private SimpleObject createObsSimpleObject() {
		SimpleObject obs = new SimpleObject();
		SimpleObject concept = new SimpleObject();
		concept.add("uuid", CONCEPT_UUID);
		obs.add("uuid", "some-uuid");
		obs.add("concept", concept);

		return obs;
	}
}
