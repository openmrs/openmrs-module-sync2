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
import org.openmrs.module.webservices.rest.web.Hyperlink;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.openmrs.module.sync2.api.utils.SyncUtils.createDefaultGson;

public class SyncHashcodeUtilsUtilsTest {

	private static final String VISIT_UUID = "visitUuid";

	private static final String PROVIDER_UUID = "providerUuid";

	private static final String PROVIDER_IDENTIFIER = "providerIdentifier";

	private static final String PRIVILEGE_UUID = "privilegeUuid";

	private static final String PRIVILEGE = "test_privilege";

	public static final String LINKS_KEY = "links";

	public static final String TEST_LINK_1 = "testLink1";

	public static final String TEST_LINK_2 = "testLink1";

	public static final String NESTED_OBJECT_KEY = "NESTED_OBJECT";

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
		String hashcode = SyncHashcodeUtils.getHashcode(simpleVisit);
		String updatedHashcode = SyncHashcodeUtils.getHashcode(updatedSimpleVisit);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(updatedHashcode);
		Assert.assertNotEquals(hashcode, updatedHashcode);
	}

	@Test
	public void hashCodesEqual() {
		String hashcode = SyncHashcodeUtils.getHashcode(simpleVisit);
		String hashcode2 = SyncHashcodeUtils.getHashcode(simpleVisit);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void hashCodesEqualWhenVoided() throws IOException {
		Visit visit = createVisit();
		// get a hashcode before the change
		String hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(visit));

		visit.setDateVoided(new Date(1540000000000L));
		visit.setVoided(true);
		visit.setVoidedBy(new User());
		visit.setVoidReason("Voided Reason");
		String hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(visit));

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void hashCodesEqualWhenRetired() throws IOException {
		Provider provider = createProvider();
		// get a hashcode before the change
		String hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(provider));

		provider.setDateRetired(new Date(1540000000000L));
		provider.setRetired(true);
		provider.setRetiredBy(new User());
		provider.setRetireReason("Retired Reason");
		String hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(provider));

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void getHashcode_shouldDeleteLinksAndReturnTheSame() throws IOException {
		SimpleObject visitSimpleObject = getSimpleObject(createVisit());

		visitSimpleObject.add(LINKS_KEY, TEST_LINK_1);
		SimpleObject nestedObject = new SimpleObject();
		nestedObject.add(LINKS_KEY, TEST_LINK_1);
		visitSimpleObject.add(NESTED_OBJECT_KEY, nestedObject);

		String hashcode = SyncHashcodeUtils.getHashcode(visitSimpleObject);

		visitSimpleObject.add(LINKS_KEY, TEST_LINK_2);
		nestedObject.add(LINKS_KEY, Arrays.asList(TEST_LINK_2));
		visitSimpleObject.add(NESTED_OBJECT_KEY, nestedObject);

		String hashcode2 = SyncHashcodeUtils.getHashcode(visitSimpleObject);

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	@Test
	public void doNotThrowAnErrorWhileObjectDoNoHaveDatabaseId() throws IOException {
		Privilege privilege = createPrivilege();
		// get a hashcode before the change
		String hashcode = SyncHashcodeUtils.getHashcode(getSimpleObject(privilege));
		String hashcode2 = SyncHashcodeUtils.getHashcode(getSimpleObject(privilege));

		Assert.assertNotNull(hashcode);
		Assert.assertNotNull(hashcode2);
		Assert.assertEquals(hashcode, hashcode2);
	}

	private Visit createVisit() {
		Visit visit = new Visit();
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
		SimpleObject so = SimpleObject.parseJson(getJson(object));
		addLink(so);
		return moveAuditToAuditInfo(so);
	}

	private SimpleObject moveAuditToAuditInfo(SimpleObject so) throws IOException {
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

	private void addLink(SimpleObject so) {
		so.add("links", new Hyperlink("self", "."));
	}

	private String getJson(Object object) {
		Gson defaultJsonParser = createDefaultGson();
		return defaultJsonParser.toJson(object);
	}
}
