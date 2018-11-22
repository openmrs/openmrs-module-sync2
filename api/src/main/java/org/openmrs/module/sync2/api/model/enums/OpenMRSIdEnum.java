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
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.TestOrder;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.HashMap;
import java.util.Map;

public enum OpenMRSIdEnum {
	LOCATION("locationId", Location.class),
	OBSERVATION("obsId", Obs.class),
	ENCOUNTER("encounterId", Encounter.class),
	VISIT("visitId", Visit.class),
	PROVIDER("providerId", Provider.class),
	DRUG_ORDER("drugId", DrugOrder.class),
	TEST_ORDER("formId", TestOrder.class),
	FORM("orderId", Form.class),
	PATIENT_PROGRAM("patientProgramId", PatientProgram.class),
	PERSON("personId", Person.class),
	PATIENT("patientId", Patient.class),
	RELATIONSHIP("relationshipId", Relationship.class),
	COHORT("cohortId", Cohort.class),
	VISIT_TYPE("visitTypeId", VisitType.class),
	USER("userId", User.class),
	PROGRAM("programId", Program.class),
	AUDIT_MESSAGE("id", AuditMessage.class);

	private static final Map<Class, OpenMRSIdEnum> MAP;

	private final String idName;

	private final Class clazz;

	OpenMRSIdEnum(String idName, Class clazz) {
		this.idName = idName;
		this.clazz = clazz;
	}

	public String getName() {
		return idName;
	}

	public Class getClazz() {
		return clazz;
	}

	static {
		MAP = new HashMap<>();
		MAP.put(LOCATION.getClazz(), LOCATION);
		MAP.put(OBSERVATION.getClazz(), OBSERVATION);
		MAP.put(ENCOUNTER.getClazz(), ENCOUNTER);
		MAP.put(VISIT.getClazz(), VISIT);
		MAP.put(PROVIDER.getClazz(), PROVIDER);
		MAP.put(DRUG_ORDER.getClazz(), DRUG_ORDER);
		MAP.put(TEST_ORDER.getClazz(), TEST_ORDER);
		MAP.put(FORM.getClazz(), FORM);
		MAP.put(PATIENT_PROGRAM.getClazz(), PATIENT_PROGRAM);
		MAP.put(PERSON.getClazz(), PERSON);
		MAP.put(PATIENT.getClazz(), PATIENT);
		MAP.put(RELATIONSHIP.getClazz(), RELATIONSHIP);
		MAP.put(COHORT.getClazz(), COHORT);
		MAP.put(VISIT_TYPE.getClazz(), VISIT_TYPE);
		MAP.put(USER.getClazz(), USER);
		MAP.put(PROGRAM.getClazz(), PROGRAM);
		MAP.put(AUDIT_MESSAGE.getClazz(), AUDIT_MESSAGE);
	}

	public static OpenMRSIdEnum getByClass(Class clazz) {
		return MAP.get(clazz);
	}
}
