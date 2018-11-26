package org.openmrs.module.sync2.api.conflict.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeMessageEnum;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.mother.SimpleObjectMother;
import org.openmrs.module.webservices.rest.SimpleObject;

public class NewIsTheBestMergeBehaviourImplTest {

	private static final String LATER_DATE = "2016-02-29T09:00:56.000+0000";

	private static final String EARLIER_DATE = "2016-02-28T09:00:56.000+0000";

	private static final String INVALID_DATE = "2018-11-23";

	private NewIsTheBestMergeBehaviourImpl newIsTheBestMergeBehaviour;

	@Before
	public void  setUp() {
		newIsTheBestMergeBehaviour = new NewIsTheBestMergeBehaviourImpl();
	}

	@Test
	public void resolveDiff_shouldReturnSuccessIfDateChangedNotExitAndChooseTheForeign() {
		SyncObject localObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged
				(null,  false, false));
		SyncObject foreignObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				null,  false, false));
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SyncObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.FOREIGN_SAVE_MESSAGE, actual.getMessage());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessIfOnlyForeignHasDateChanged() {
		SyncObject localObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				null,  false, false));
		SyncObject foreignObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				LATER_DATE,  true, false));
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SyncObject.class, foreignObj, localObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.FOREIGN_SAVE_MESSAGE, actual.getMessage());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseTheNewest() {
		SyncObject localObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				LATER_DATE,  true, false));
		SyncObject foreignObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				EARLIER_DATE,  true, false));
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SyncObject.class, foreignObj, localObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.LOCAL_SAVE_MESSAGE, actual.getMessage());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseLocalIfDatesAreTheSame() {
		SyncObject localObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				EARLIER_DATE,  true, false));
		SyncObject foreignObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				EARLIER_DATE,  true, false));
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SyncObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.LOCAL_SAVE_MESSAGE, actual.getMessage());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnMergeConflictIfInvalidDateFormat() {
		SyncObject localObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				EARLIER_DATE,  true, false));
		SyncObject foreignObj = getSyncObject(SimpleObjectMother.createInstanceWithDateChanged(
				INVALID_DATE,  true, false));
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SyncObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeConflict);
		Assert.assertEquals(MergeMessageEnum.CONFLICT, actual.getMessage());
	}

	private SyncObject getSyncObject(SimpleObject simpleObject) {
		return new SyncObject(simpleObject, simpleObject);
	}
}
