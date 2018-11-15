package org.openmrs.module.sync2.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.sync2.api.dao.MergeConflictDao;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.mother.MergeConflictMother;

public class MergeConflictServiceImplTest {

	private static final int MERGE_CONFLICT_ID = 1;

	private MergeConflict mergeConflict;

	@Mock
	private MergeConflictDao mergeConflictDao;

	@InjectMocks
	private MergeConflictServiceImpl mergeConflictService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mergeConflict = MergeConflictMother.createInstance(MERGE_CONFLICT_ID);

		Mockito.when(mergeConflictDao.getById(MERGE_CONFLICT_ID)).thenReturn(mergeConflict);
		Mockito.when(mergeConflictDao.getByUuid(MergeConflictMother.UUID)).thenReturn(mergeConflict);
		Mockito.when(mergeConflictDao.save(Mockito.any())).thenReturn(mergeConflict);
	}

	@Test
	public void save_shouldSaveObject() {
		MergeConflict expected = MergeConflictMother.createInstance(MERGE_CONFLICT_ID);
		MergeConflict actual = mergeConflictService.save(mergeConflict);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getById_shouldFetchCorrectObject() {
		MergeConflict expected = MergeConflictMother.createInstance(MERGE_CONFLICT_ID);
		MergeConflict actual = mergeConflictService.getById(MERGE_CONFLICT_ID);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getByUuid_shouldFetchCorrectObject() {
		MergeConflict expected = MergeConflictMother.createInstance(MERGE_CONFLICT_ID);
		MergeConflict actual = mergeConflictService.getByUuid(MergeConflictMother.UUID);
		Assert.assertEquals(expected, actual);
	}
}
