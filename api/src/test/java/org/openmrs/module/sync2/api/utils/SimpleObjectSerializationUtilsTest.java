package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.sync2.api.mother.SimpleObjectMother;
import org.openmrs.module.webservices.rest.SimpleObject;

public class SimpleObjectSerializationUtilsTest {

	private static final String EXPECTED_SIMPLE_OBJECT_FILE = "expectedSimpleObject.json";

	private static final String TEST_DATE_CHANGE = "2018-11-26";

	@Test
	public void serialize_shouldCreateValidJson() {
		String expected = SyncConfigurationUtils.readResourceFile(EXPECTED_SIMPLE_OBJECT_FILE);
		SimpleObject simpleObject = SimpleObjectMother.createInstanceWithDateChanged(
				TEST_DATE_CHANGE, true, false);
		String actual = SimpleObjectSerializationUtils.serialize(simpleObject);
		actual += '\n';
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void deserialize_shouldCreateValidObject() {
		SimpleObject expected = SimpleObjectMother.createInstanceWithDateChanged(
				TEST_DATE_CHANGE, true, true);
		String json = SyncConfigurationUtils.readResourceFile(EXPECTED_SIMPLE_OBJECT_FILE);
		SimpleObject actual = SimpleObjectSerializationUtils.deserialize(json);
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void cloneMethod_shouldCorrectlyCloneObject() {
		SimpleObject expected = SimpleObjectMother.createInstanceWithDateChanged(
				TEST_DATE_CHANGE, true, true);
		SimpleObject actual = SimpleObjectSerializationUtils.clone(expected);
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected, actual);
	}
}
