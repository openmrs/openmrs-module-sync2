package org.openmrs.module.sync2.api.model.enums;

import org.openmrs.Allergy;
import org.openmrs.Cohort;

import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.TestOrder;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.HashMap;
import java.util.Map;

public enum CategoryEnum {
	LOCATION(FHIRConstants.CATEGORY_LOCATION, Location.class),
	OBSERVATION(FHIRConstants.CATEGORY_OBSERVATION, Obs.class),
	ENCOUNTER(FHIRConstants.CATEGORY_ENCOUNTER, Encounter.class),
	VISIT(FHIRConstants.CATEGORY_VISIT, Visit.class),
	PROVIDER(FHIRConstants.CATEGORY_PROVIDER, Provider.class),
	DRUG_ORDER(FHIRConstants.CATEGORY_DRUG_ORDER, DrugOrder.class),
	TEST_ORDER(FHIRConstants.CATEGORY_TEST_ORDER, TestOrder.class),
	FORM("form", Form.class),
	PATIENT_PROGRAM("patient_program", PatientProgram.class),
	PERSON(FHIRConstants.CATEGORY_PERSON, Person.class),
	PATIENT(FHIRConstants.CATEGORY_PATIENT, Patient.class),
	RELATIONSHIP("relationship", Relationship.class),
	COHORT(FHIRConstants.CATEGORY_COHORT, Cohort.class),
	VISIT_TYPE("visit_type", VisitType.class),
	USER("user", User.class),
	PROGRAM("program", Program.class),
	PRIVILEGE("privilege", Privilege.class),
	AUDIT_MESSAGE("audit_message", AuditMessage.class),
	ALLERGY(FHIRConstants.CATEGORY_ALLERGY, Allergy.class);

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
		MAP.put(ALLERGY.getCategory(), ALLERGY);
	}

	public static CategoryEnum getByCategory(String category) {
		return MAP.get(category);
	}
}
