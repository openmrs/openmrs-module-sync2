package org.openmrs.module.sync2.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.mother.SimpleObjectMother;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.List;

public class AbstractSynchronizationServiceTest {

	private static final String TEST_UUID = "test_uuid";

	private static final String TEST_VALUE = "test value";

	private static final String TEST_VALUE_2 = "test value2";

	public static final String VOIDED_KEY = "voided";

	private AbstractSynchronizationService abstractSynchronizationService;

	@Before
	public void setUp() {
		abstractSynchronizationService = Mockito.mock(AbstractSynchronizationService.class, Mockito.CALLS_REAL_METHODS);
	}

	@Test
	public void determineActions_shouldReturnCREATED() {
		List<String> actual = abstractSynchronizationService.determineActions(
				null,
				createPatientInstance(false));
		Assert.assertTrue(actual.contains(SyncConstants.ACTION_CREATED));
	}

	@Test
	public void determineActions_shouldReturnUPDATED() {
		List<String> actual = abstractSynchronizationService.determineActions(
				createPatientInstance(false),
				createPatientInstance(false));
		Assert.assertTrue(actual.contains(SyncConstants.ACTION_UPDATED));
	}

	@Test
	public void determineActions_shouldReturnVOIDED() {
		List<String> actual = abstractSynchronizationService.determineActions(
				createPatientInstance(false),
				createPatientInstance(true));
		Assert.assertTrue(actual.contains(SyncConstants.ACTION_VOIDED));
	}

	@Test
	public void shouldSynchronize_shouldReturnTrueIfCrateOrUpdateAndNewObjectIsNull() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(oldObject, null,
				SyncConstants.ACTION_CREATED));
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(oldObject, null,
				SyncConstants.ACTION_UPDATED));
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, null,
				SyncConstants.ACTION_VOIDED));
	}

	@Test
	public void shouldSynchronize_shouldReturnTrueIfNotUpdateAndOldObjectIsNull() {
		SimpleObject newObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(null, newObject,
				SyncConstants.ACTION_CREATED));
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(null, newObject,
				SyncConstants.ACTION_UPDATED));
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(null, newObject,
				SyncConstants.ACTION_VOIDED));
	}

	@Test
	public void shouldSynchronize_shouldReturnFalseIfObjectsAreEquals() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		SimpleObject newObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_CREATED));
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_UPDATED));
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_VOIDED));
	}

	@Test
	public void shouldSynchronize_shouldReturnTrueIfUpdateOrVoidedAndObjectsAreNotEquals() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		SimpleObject newObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE_2);
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_UPDATED));
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_VOIDED));
	}

	@Test
	public void shouldSynchronize_shouldReturnFalseIfDeleteAndNewObjectIsNull() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, null,
				SyncConstants.ACTION_DELETED));
	}

	@Test
	public void shouldSynchronize_shouldReturnFalseIfUpdateAndOldObjectIsNull() {
		SimpleObject newObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(null, newObject,
				SyncConstants.ACTION_UPDATED));
	}

	@Test
	public void shouldSynchronize_shouldReturnTrueIfCreateAndNewObjectIsNull() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(oldObject, null,
				SyncConstants.ACTION_CREATED));
	}

	@Test
	public void shouldSynchronize_shouldReturnFalseIfCreateAndObjectsAreNotNull() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		SimpleObject newObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE_2);
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_CREATED));
	}

	@Test
	public void shouldSynchronize_shouldReturnTrueIfCreateAndObjectsAreNull() {
		SimpleObject oldObject = null;
		SimpleObject newObject = null;
		Assert.assertTrue(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_CREATED));
	}

	@Test
	public void shouldSynchronize_shouldReturnTrueIfUpdateOnVoidedOldObject() {
		SimpleObject oldObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE);
		oldObject.put(VOIDED_KEY, true);
		SimpleObject newObject = SimpleObjectMother.createInstance(TEST_UUID, TEST_VALUE_2);
		Assert.assertFalse(abstractSynchronizationService.shouldSynchronize(oldObject, newObject,
				SyncConstants.ACTION_UPDATED));
	}

	private Patient createPatientInstance(boolean voided) {
		Patient patient = new Patient();
		if (voided) {
			patient.setVoided(true);
		}
		return patient;
	}
}
