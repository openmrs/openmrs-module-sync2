package org.openmrs.module.sync2.api.conflict.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeMessageEnum;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
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
		SimpleObject localObj = SimpleObjectMother.createInstanceWithDateChanged(null,  false);
		SimpleObject foreignObj = SimpleObjectMother.createInstanceWithDateChanged(null,  false);
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.FOREIGN_SAVE_MESSAGE, actual.getMessage());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessIfOnlyForeignHasDateChanged() {
		SimpleObject localObj = SimpleObjectMother.createInstanceWithDateChanged(null,  false);
		SimpleObject foreignObj = SimpleObjectMother.createInstanceWithDateChanged(LATER_DATE,  true);
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.FOREIGN_SAVE_MESSAGE, actual.getMessage());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseTheNewest() {
		SimpleObject localObj = SimpleObjectMother.createInstanceWithDateChanged(LATER_DATE,  true);
		SimpleObject foreignObj = SimpleObjectMother.createInstanceWithDateChanged(EARLIER_DATE,  true);
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.LOCAL_SAVE_MESSAGE, actual.getMessage());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnSuccessAndChooseLocalIfDatesAreTheSame() {
		SimpleObject localObj = SimpleObjectMother.createInstanceWithDateChanged(EARLIER_DATE,  true);
		SimpleObject foreignObj = SimpleObjectMother.createInstanceWithDateChanged(EARLIER_DATE,  true);
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeSuccess);
		Assert.assertEquals(MergeMessageEnum.LOCAL_SAVE_MESSAGE, actual.getMessage());
		Assert.assertTrue(((MergeSuccess) actual).shouldUpdateLocal());
		Assert.assertFalse(((MergeSuccess) actual).shouldUpdateForeign());
	}

	@Test
	public void resolveDiff_shouldReturnMergeConflictIfInvalidDateFormat() {
		SimpleObject localObj = SimpleObjectMother.createInstanceWithDateChanged(EARLIER_DATE,  true);
		SimpleObject foreignObj = SimpleObjectMother.createInstanceWithDateChanged(INVALID_DATE,  true);
		MergeResult actual = newIsTheBestMergeBehaviour.resolveDiff(SimpleObject.class, localObj, foreignObj);
		Assert.assertTrue(actual instanceof MergeConflict);
		Assert.assertEquals(MergeMessageEnum.CONFLICT, actual.getMessage());
	}
}
