package org.openmrs.module.sync2.api.conflict.impl;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeMessageEnum;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.mother.ParentObjectHashcodeMother;
import org.openmrs.module.sync2.api.mother.SimpleObjectMother;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.UUID;

public class ConflictMergeBehaviourImplTest {

	private static final String TEST_VALUE = "Test value";

	private static final String TEST_VALUE_2 = "Test value 2";

	private static final String TEST_VALID_HASHCODE_VALUE = "Test valid value";

	private static final String VALID_UUID = "valid_uuid";

	private static final String TEST_HASHCODE = "f920e06a-a331-4a7e-9226-ce03df96ae47";

	private static final String INVALID_UUID = "INVALID_UUID";

	@Mock
	ParentObjectHashcodeService parentObjectHashcodeService;

	@InjectMocks
	private ConflictMergeBehaviourImpl conflictMergeBehaviour;

	@Before
	public void  setUp() {
		MockitoAnnotations.initMocks(this);
		ParentObjectHashcode parentObjectHashcode = ParentObjectHashcodeMother.createInstance(VALID_UUID, TEST_HASHCODE);
		Mockito.when(parentObjectHashcodeService.getByObjectUuid(Mockito.anyString()))
				.thenReturn(ParentObjectHashcodeMother.createInstance(null, null));
		Mockito.when(parentObjectHashcodeService.getByObjectUuid(VALID_UUID)).thenReturn(parentObjectHashcode);
		Mockito.when(parentObjectHashcodeService.getByObjectUuid(INVALID_UUID)).thenReturn(null);
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseForeignIfParentHashcodeNotExist() {
		SimpleObject localObj = SimpleObjectMother.createInstance(INVALID_UUID, TEST_VALUE);
		SimpleObject foreignObj = SimpleObjectMother.createInstance(INVALID_UUID, TEST_VALUE_2);
		MergeResult actual = conflictMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.FOREIGN_SAVE_MESSAGE, actual.getMessage());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseLocalIfOnlyLocalChanged() {
		SimpleObject localObj = SimpleObjectMother.createInstance(VALID_UUID, TEST_VALUE);
		SimpleObject foreignObj = SimpleObjectMother.createInstance(VALID_UUID, TEST_VALID_HASHCODE_VALUE);
		MergeResult actual = conflictMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.LOCAL_SAVE_MESSAGE, actual.getMessage());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseForeignIfOnlyForeignChanged() {
		SimpleObject localObj = SimpleObjectMother.createInstance(VALID_UUID, TEST_VALID_HASHCODE_VALUE);
		SimpleObject foreignObj = SimpleObjectMother.createInstance(VALID_UUID, TEST_VALUE_2);
		MergeResult actual = conflictMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.FOREIGN_SAVE_MESSAGE, actual.getMessage());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnConflictWhenBothObjectsChanged() {
		SimpleObject localObj = SimpleObjectMother.createInstance(VALID_UUID, TEST_VALUE);
		SimpleObject foreignObj = SimpleObjectMother.createInstance(VALID_UUID, TEST_VALUE_2);
		MergeResult actual = conflictMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeConflict);
		Assert.assertEquals(MergeMessageEnum.CONFLICT, actual.getMessage());
	}
}
