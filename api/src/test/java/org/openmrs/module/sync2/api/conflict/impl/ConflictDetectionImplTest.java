package org.openmrs.module.sync2.api.conflict.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.mother.ParentObjectHashcodeMother;
import org.openmrs.module.sync2.api.mother.SimpleObjectMother;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.webservices.rest.SimpleObject;

public class ConflictDetectionImplTest {

	private static final String INVALID_HASHCODE_VALUE = "Test value";

	private static final String VALID_HASHCODE_VALUE = "Test valid value";

	private static final String VALID_UUID = "valid_uuid";

	private static final String INVALID_UUID = "INVALID_UUID";

	private static final String TEST_HASHCODE = "96c4ae94164efdbfe4a86fe834ac13a866da78252f71d3ee0bec2dce3743"
			+ "7e135c6c52ee4857837589abe06691536153c749d7da4f7fc8cae7cfb35878d154e1";

	@Mock
	private ParentObjectHashcodeService parentObjectHashcodeService;

	@InjectMocks
	private  ConflictDetectionImpl conflictDetection;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ParentObjectHashcode parentObjectHashcode = ParentObjectHashcodeMother
				.createInstance(VALID_UUID, TEST_HASHCODE);
		Mockito.when(parentObjectHashcodeService.getByObjectUuid(Mockito.anyString()))
				.thenReturn(ParentObjectHashcodeMother.createInstance(null, null));
		Mockito.when(parentObjectHashcodeService.getByObjectUuid(VALID_UUID)).thenReturn(parentObjectHashcode);
		Mockito.when(parentObjectHashcodeService.getByObjectUuid(INVALID_UUID)).thenReturn(null);
	}

	@Test
	public void detectConflict_shouldReturnFalseIfParentHashcodeNotExist() {
		SimpleObject currentObj = SimpleObjectMother.createInstance(INVALID_UUID, INVALID_HASHCODE_VALUE);
		SimpleObject newObj = SimpleObjectMother.createInstance(INVALID_UUID, INVALID_HASHCODE_VALUE);
		Assert.assertFalse(conflictDetection.detectConflict(currentObj, newObj));
	}

	@Test
	public void detectConflict_shouldReturnFalseIfObjectsHaveNotChanged() {
		SimpleObject currentObj = SimpleObjectMother.createInstance(VALID_UUID, VALID_HASHCODE_VALUE);
		SimpleObject newObj = SimpleObjectMother.createInstance(VALID_UUID, VALID_HASHCODE_VALUE);
		Assert.assertFalse(conflictDetection.detectConflict(currentObj, newObj));
	}

	@Test
	public void detectConflict_shouldReturnTrueIfCurrentObjectChanged() {
		SimpleObject currentObj = SimpleObjectMother.createInstance(VALID_UUID, INVALID_HASHCODE_VALUE);
		SimpleObject newObj = SimpleObjectMother.createInstance(VALID_UUID, VALID_HASHCODE_VALUE);
		Assert.assertTrue(conflictDetection.detectConflict(currentObj, newObj));
	}

	@Test
	public void detectConflict_shouldReturnTrueIfNewObjectChanged() {
		SimpleObject currentObj = SimpleObjectMother.createInstance(VALID_UUID, VALID_HASHCODE_VALUE);
		SimpleObject newObj = SimpleObjectMother.createInstance(VALID_UUID, INVALID_HASHCODE_VALUE);
		Assert.assertTrue(conflictDetection.detectConflict(currentObj, newObj));
	}
}
