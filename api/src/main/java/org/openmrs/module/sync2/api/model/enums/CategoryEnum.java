package org.openmrs.module.sync2.api.model.enums;

import org.openmrs.Cohort;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.TestOrder;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.HashMap;
import java.util.Map;

public enum CategoryEnum {
	LOCATION(SyncCategoryConstants.CATEGORY_LOCATION, Location.class),
	OBSERVATION(SyncCategoryConstants.CATEGORY_OBSERVATION, Obs.class),
	ENCOUNTER(SyncCategoryConstants.CATEGORY_ENCOUNTER, Encounter.class),
	VISIT(SyncCategoryConstants.CATEGORY_VISIT, Visit.class),
	PROVIDER(SyncCategoryConstants.CATEGORY_PROVIDER, Provider.class),
	DRUG_ORDER(SyncCategoryConstants.CATEGORY_DRUG_ORDER, DrugOrder.class),
	TEST_ORDER(SyncCategoryConstants.CATEGORY_TEST_ORDER, TestOrder.class),
	FORM(SyncCategoryConstants.CATEGORY_FORM, Form.class),
	PATIENT_PROGRAM(SyncCategoryConstants.CATEGORY_PATIENT_PROGRAM, PatientProgram.class),
	PERSON(SyncCategoryConstants.CATEGORY_PERSON, Person.class),
	PATIENT(SyncCategoryConstants.CATEGORY_PATIENT, Patient.class),
	RELATIONSHIP(SyncCategoryConstants.CATEGORY_RELATIONSHIP, Relationship.class),
	COHORT(SyncCategoryConstants.CATEGORY_COHORT, Cohort.class),
	VISIT_TYPE(SyncCategoryConstants.CATEGORY_VISIT_TYPE, VisitType.class),
	USER(SyncCategoryConstants.CATEGORY_USER, User.class),
	PROGRAM(SyncCategoryConstants.CATEGORY_PROGRAM, Program.class),
	PRIVILEGE(SyncCategoryConstants.CATEGORY_PRIVILEGE, Privilege.class),
	AUDIT_MESSAGE(SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE, AuditMessage.class),
	PERSON_ADDRESS(SyncCategoryConstants.CATEGORY_PERSON_ADDRESS, PersonAddress.class),
	PERSON_NAME(SyncCategoryConstants.CATEGORY_PERSON_NAME, PersonName.class);

	private static final Map<String, CategoryEnum> MAP;

	private final String category;

	private final Class clazz;

	CategoryEnum(String category, Class clazz) {
		this.category = category;
		this.clazz = clazz;
	}

	public String getCategory() {
		return category;
	}

	public Class getClazz() {
		return clazz;
	}

	static {
		MAP = new HashMap<>();
		MAP.put(LOCATION.getCategory(), LOCATION);
		MAP.put(OBSERVATION.getCategory(), OBSERVATION);
		MAP.put(ENCOUNTER.getCategory(), ENCOUNTER);
		MAP.put(VISIT.getCategory(), VISIT);
		MAP.put(PROVIDER.getCategory(), PROVIDER);
		MAP.put(DRUG_ORDER.getCategory(), DRUG_ORDER);
		MAP.put(TEST_ORDER.getCategory(), TEST_ORDER);
		MAP.put(FORM.getCategory(), FORM);
		MAP.put(PATIENT_PROGRAM.getCategory(), PATIENT_PROGRAM);
		MAP.put(PERSON.getCategory(), PERSON);
		MAP.put(PATIENT.getCategory(), PATIENT);
		MAP.put(RELATIONSHIP.getCategory(), RELATIONSHIP);
		MAP.put(COHORT.getCategory(), COHORT);
		MAP.put(VISIT_TYPE.getCategory(), VISIT_TYPE);
		MAP.put(USER.getCategory(), USER);
		MAP.put(PROGRAM.getCategory(), PROGRAM);
		MAP.put(PRIVILEGE.getCategory(), PRIVILEGE);
		MAP.put(AUDIT_MESSAGE.getCategory(), AUDIT_MESSAGE);
		MAP.put(PERSON_ADDRESS.getCategory(), PERSON_ADDRESS);
		MAP.put(PERSON_NAME.getCategory(), PERSON_NAME);
	}

	public static CategoryEnum getByCategory(String category) {
		return MAP.get(category);
	}

	private static class Constants {


	}
}
