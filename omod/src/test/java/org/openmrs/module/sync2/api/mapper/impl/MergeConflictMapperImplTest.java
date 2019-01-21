package org.openmrs.module.sync2.api.mapper.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.merge.MergeMessageEnum;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.utils.SimpleObjectSerializationUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.serialization.SerializationException;
import org.openmrs.serialization.SimpleXStreamSerializer;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class MergeConflictMapperImplTest extends BaseModuleWebContextSensitiveTest {

	private static final String TEST_PATIENT_UUID = "Test patient uuid";

	public static final String THE_SAME_UUID = "the same uuid";

	private MergeConflict mergeConflictDao;

	private org.openmrs.module.fhir.api.merge.MergeConflict mergeConflictDto;

	private MergeConflictMapper mergeConflictMapper;

	@Before
	public void setUp() throws SerializationException {
		Patient patient = new Patient();
		patient.setUuid(TEST_PATIENT_UUID);

		mergeConflictDao = buildDao(patient, Patient.class);

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
	public void map_shouldCorrectlyMapSimpleObjects() throws SerializationException {
		SimpleObject simpleObject = buildSimpleObject();
		MergeConflict expectedDao = buildDao(simpleObject, SimpleObject.class);

		org.openmrs.module.fhir.api.merge.MergeConflict actualDto = mergeConflictMapper.map(expectedDao);
		Assert.assertNotNull(actualDto);

		MergeConflict actualDao = mergeConflictMapper.map(actualDto);
		Assert.assertNotNull(actualDao);

		//The objects UUID is not important in this case
		expectedDao.setUuid(THE_SAME_UUID);
		actualDao.setUuid(THE_SAME_UUID);
		Assert.assertEquals(expectedDao, actualDao);
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

	private MergeConflict buildDao(Object object, Class clazz) throws SerializationException {
		MergeConflict result = new MergeConflict();

		byte[] serializedObject = null;
		if (clazz.isAssignableFrom(SimpleObject.class)) {
			serializedObject = SimpleObjectSerializationUtils.serialize((SimpleObject) object).getBytes();
		} else {
			serializedObject = Context.getSerializationService().serialize(object, SimpleXStreamSerializer.class).getBytes();
		}

		result.setOrgLocal(serializedObject);
		result.setOrgForeign(serializedObject);
		result.setMessage(MergeMessageEnum.CONFLICT.getValue());
		result.setFullClassName(clazz.getCanonicalName());
		return result;
	}

	private SimpleObject buildSimpleObject() {
		SimpleObject simpleObject = new SimpleObject();
		simpleObject.add("key", "value");
		simpleObject.add("key2", "value2");
		simpleObject.add("key3", "value3");
		simpleObject.add("key4", "value4");
		return simpleObject;
	}
}
