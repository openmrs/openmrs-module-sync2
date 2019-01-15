package org.openmrs.module.sync2.api.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.sync2.api.dao.ParentObjectHashcodeDao;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.mother.ParentObjectHashcodeMother;

public class ParentObjectHashcodeServiceImplTest {

	private static final int OBJECT_ID = 1;

	private ParentObjectHashcode parentObjectHashcode;

	@Mock
	private ParentObjectHashcodeDao parentObjectHashcodeDao;

	@InjectMocks
	private ParentObjectHashcodeServiceImpl parentObjectHashcodeService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		parentObjectHashcode = ParentObjectHashcodeMother.createInstance(OBJECT_ID);

		Mockito.when(parentObjectHashcodeDao.getById(OBJECT_ID)).thenReturn(parentObjectHashcode);
		Mockito.when(parentObjectHashcodeDao.getByUuid(ParentObjectHashcodeMother.UUID))
				.thenReturn(parentObjectHashcode);
		Mockito.when(parentObjectHashcodeDao.getByObjectUuid(ParentObjectHashcodeMother.OBJECT_UUID))
				.thenReturn(parentObjectHashcode);
		Mockito.when(parentObjectHashcodeDao.save((ParentObjectHashcode) Mockito.any())).thenReturn(parentObjectHashcode);
	}

	@Test
	public void save_shouldSaveObject() {
		ParentObjectHashcode expected = ParentObjectHashcodeMother.createInstance(OBJECT_ID);
		ParentObjectHashcode actual = parentObjectHashcodeService.save(parentObjectHashcode);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getById_shouldFetchCorrectObject() {
		ParentObjectHashcode expected = ParentObjectHashcodeMother.createInstance(OBJECT_ID);
		ParentObjectHashcode actual = parentObjectHashcodeService.getById(OBJECT_ID);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getByUuid_shouldFetchCorrectObject() {
		ParentObjectHashcode expected = ParentObjectHashcodeMother.createInstance(OBJECT_ID);
		ParentObjectHashcode actual = parentObjectHashcodeService.getByUuid(ParentObjectHashcodeMother.UUID);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getByObjectUuid_shouldFetchCorrectObject() {
		ParentObjectHashcode expected = ParentObjectHashcodeMother.createInstance(OBJECT_ID);
		ParentObjectHashcode actual = parentObjectHashcodeService.getByObjectUuid(ParentObjectHashcodeMother.OBJECT_UUID);
		Assert.assertEquals(expected, actual);
	}

}
