package org.openmrs.module.sync2.api.mapper.impl;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.fhir.api.merge.MergeMessageEnum;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.MergeConflict;

public class MergeConflictMapperImplTest {

	private static final String TEST_PATIENT_UUID = "Test patient uuid";

	private MergeConflict mergeConflictDao;

	private org.openmrs.module.fhir.api.merge.MergeConflict mergeConflictDto;

	private MergeConflictMapper mergeConflictMapper;

	@Before
	public void setUp() {
		Patient patient = new Patient();
		patient.setUuid(TEST_PATIENT_UUID);

		mergeConflictDao = new MergeConflict();
		mergeConflictDao.setOrgLocal(SerializationUtils.serialize(patient));
		mergeConflictDao.setOrgForeign(SerializationUtils.serialize(patient));
		mergeConflictDao.setMessage(MergeMessageEnum.CONFLICT.getValue());
		mergeConflictDao.setFullClassName("org.openmrs.Patient");

		mergeConflictDto = new org.openmrs.module.fhir.api.merge.MergeConflict(Patient.class, patient, patient);

		mergeConflictMapper = new MergeConflictMapperImpl();
	}

	@Test
	public void map_shouldReturnCorrectMergerConflictDto() {
		org.openmrs.module.fhir.api.merge.MergeConflict actualDto = mergeConflictMapper.map(this.mergeConflictDao);
		Assert.assertNotNull(actualDto);
		Assert.assertEquals(mergeConflictDto, mergeConflictDto);
		Assert.assertTrue(actualDto.getOrgForeign() instanceof Patient);
		Assert.assertTrue(actualDto.getOrgLocal() instanceof Patient);
		Assert.assertEquals(((Patient) mergeConflictDto.getOrgForeign()).getUuid(), ((Patient) actualDto.getOrgForeign()).getUuid());
		Assert.assertEquals(((Patient) mergeConflictDto.getOrgLocal()).getUuid(), ((Patient) actualDto.getOrgLocal()).getUuid());
	}

	@Test
	public void map_shouldReturnCorrectMergeConflictDao() {
		MergeConflict actualDao = mergeConflictMapper.map(mergeConflictDto);
		Assert.assertNotNull(actualDao);
		Assert.assertEquals(mergeConflictDao.getMessage(), actualDao.getMessage());
		Assert.assertEquals(mergeConflictDao.getFullClassName(), actualDao.getFullClassName());
		Assert.assertArrayEquals(mergeConflictDao.getOrgForeign(), actualDao.getOrgForeign());
		Assert.assertArrayEquals(mergeConflictDao.getOrgLocal(), actualDao.getOrgLocal());
	}

	@Test
	public void map_shouldReturnTheSame() {
		org.openmrs.module.fhir.api.merge.MergeConflict actualDto = mergeConflictMapper.map(this.mergeConflictDao);
		Assert.assertNotNull(actualDto);
		MergeConflict actualDao = mergeConflictMapper.map(actualDto);
		Assert.assertNotNull(actualDao);
		Assert.assertEquals(mergeConflictDao.getMessage(), actualDao.getMessage());
		Assert.assertEquals(mergeConflictDao.getFullClassName(), actualDao.getFullClassName());
		Assert.assertArrayEquals(mergeConflictDao.getOrgForeign(), actualDao.getOrgForeign());
		Assert.assertArrayEquals(mergeConflictDao.getOrgLocal(), actualDao.getOrgLocal());
	}
}
