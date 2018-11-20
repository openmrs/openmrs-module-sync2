package org.openmrs.module.sync2.api.utils;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Privilege;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static org.openmrs.module.sync2.api.utils.SyncUtils.createDefaultGson;

public class SyncHashcodeUtilsUtilsTest {

	private static final String VISIT_UUID = "visitUuid";

	private static final String PROVIDER_UUID = "providerUuid";

	private static final String PROVIDER_IDENTIFIER = "providerIdentifier";

	private static final String PRIVILEGE_UUID = "privilegeUuid";

	private static final String PRIVILEGE = "test_privilege";

	private static final int ID = 1;

	private static final int UPDATED_ID = 2;

	private SimpleObject simpleVisit;

	private SimpleObject updatedSimpleVisit;

	@Before
	public void setUp() throws IOException {
		simpleVisit = getSimpleObject(createVisit());
		Visit updatedVisit = createVisit();
		updatedVisit.setLocation(new Location());
		updatedSimpleVisit = getSimpleObject(updatedVisit);
	}

	@Test
	public void hashCodesNotEqual() {
		UUID hashcode = SyncHashcodeUtils.getHashcode(simpleVisit, Visit.class);
		UUID updatedHashcode = SyncHashcodeUtils.getHashcode(updatedSimpleVisit, Visit.class);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(updatedHashcode);
		Assert.assertNotEquals(hashcode, updatedHashcode);
	}

	@Test
	public void hashCodesEqual() {
		UUID hashcode = SyncHashcodeUtils.getHashcode(simpleVisit, Visit.class);
		UUID hashcode2 = SyncHashcodeUtils.getHashcode(simpleVisit, Visit.class);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void hashCodesEqualWhenVoided() throws IOException {
		Visit visit = createVisit();
		// get a hashcode before the change
		UUID hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(visit), Visit.class);

		visit.setDateVoided(new Date(1540000000000L));
		visit.setVoided(true);
		visit.setVoidedBy(new User());
		visit.setVoidReason("Voided Reason");
		UUID hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(visit), Visit.class);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void hashCodesEqualWhenRetired() throws IOException {
		Provider provider = createProvider();
		// get a hashcode before the change
		UUID hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(provider), Provider.class);

		provider.setDateRetired(new Date(1540000000000L));
		provider.setRetired(true);
		provider.setRetiredBy(new User());
		provider.setRetireReason("Retired Reason");
		UUID hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(provider), Provider.class);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void visitHashCodesEqualsWhenDatabaseIdChanged() throws IOException {
		Visit visit = createVisit();
		// get a hashcode before the change
		UUID hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(visit), Visit.class);
		visit.setId(UPDATED_ID);
		UUID hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(visit), Visit.class);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void doNotThrowAnErrorWhileObjectDoNoHaveDatabaseId() throws IOException {
		Privilege privilege = createPrivilege();
		// get a hashcode before the change
		UUID hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(privilege), Privilege.class);
		UUID hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(privilege), Privilege.class);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	private Visit createVisit() {
		Visit visit = new Visit();
		visit.setVisitId(ID);
		visit.setUuid(VISIT_UUID);
		visit.setDateChanged(new Date());
		visit.setChangedBy(new User());
		visit.setDateCreated(new Date());
		visit.setCreator(new User());
		visit.setIndication(null);
		visit.setLocation(null);
		visit.setStartDatetime(new Date());
		visit.setStopDatetime(new Date());
		visit.setVoided(false);

		return visit;
	}

	private Provider createProvider() {
		Provider provider =  new Provider();
		provider.setUuid(PROVIDER_UUID);
		provider.setDateChanged(new Date());
		provider.setChangedBy(new User());
		provider.setCreator(new User());
		provider.setDateCreated(new Date());
		provider.setPerson(new Person());
		provider.setIdentifier(PROVIDER_IDENTIFIER);
		provider.setRetired(false);
		provider.setId(ID);
		return provider;
	}

	private Privilege createPrivilege() {
		Privilege privilege = new Privilege();
		privilege.setUuid(PRIVILEGE_UUID);
		privilege.setPrivilege(PRIVILEGE);
		privilege.setDateChanged(new Date());
		privilege.setChangedBy(new User());
		privilege.setDateCreated(new Date());
		privilege.setCreator(new User());
		privilege.setRetired(false);

		return privilege;
	}

	/**
	 * Parse object in order to simulate objects pulled by SyncServices
	 *
	 * @param object to convert
	 * @return returns an object which extends SimpleObject
	 */
	private SimpleObject getSimpleObject(Object object) throws IOException {
		return moveAuditToAuditInfo(getJson(object));
	}

	private SimpleObject moveAuditToAuditInfo(String json) throws IOException {
		SimpleObject so = SimpleObject.parseJson(json);
		SimpleObject auditInfo = new SimpleObject();
		auditInfo.add("creator", so.get("creator"));
		so.removeProperty("creator");
		auditInfo.add("dateCreated", so.get("dateCreated"));
		so.removeProperty("dateCreated");
		auditInfo.add("changedBy", so.get("changedBy"));
		so.removeProperty("changedBy");
		auditInfo.add("dateChanged", so.get("dateChanged"));
		so.removeProperty("dateChanged");
		so.add("auditInfo", auditInfo);

		return so;
	}

	private String getJson(Object object) {
		Gson defaultJsonParser = createDefaultGson();
		return defaultJsonParser.toJson(object);
	}
}
