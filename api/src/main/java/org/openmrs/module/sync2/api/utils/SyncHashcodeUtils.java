package org.openmrs.module.sync2.api.utils;

import org.apache.commons.codec.digest.DigestUtils;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Cohort;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Privilege;
import org.openmrs.Program;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.Map;
import java.util.UUID;

public class SyncHashcodeUtils {

	private static final String LOCATION_ID = "locationId";
	private static final String OBS_ID = "obsId";
	private static final String ENCOUNTER_ID = "encounterId";
	private static final String VISIT_ID = "visitId";
	private static final String PROVIDER_ID = "providerId";
	private static final String DRUG_ID = "drugId";
	private static final String FORM_ID = "formId";
	private static final String ORDER_ID = "orderId";
	private static final String PATIENT_PROGRAM_ID = "patientProgramId";
	private static final String PERSON_ID = "personId";
	private static final String PATIENT_ID = "patientId";
	private static final String RELATIONSHIP_ID = "relationshipId";
	private static final String COHORT_ID = "cohortId";
	private static final String VISIT_TYPE_ID = "visitTypeId";
	private static final String USER_ID = "userId";
	private static final String PROGRAM_ID = "programId";
	private static final String AUDIT_MESSAGE_ID = "id";

	private static final SimpleObjectMessageConverter converter = new SimpleObjectMessageConverter();

	private static final String regex = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})";

	private static final String replacement = "$1-$2-$3-$4-$5";

	public static UUID getHashcode(SimpleObject simpleObject, Class<?> clazz) {
		return getUuidFromString(converter.convertToJson(removeFields(simpleObject, clazz)));
	}

	private static SimpleObject copySimpleObject(SimpleObject simpleObject) {
		SimpleObject result = new SimpleObject();
		for (Map.Entry<String, Object> entry : simpleObject.entrySet()) {
			result.add(entry.getKey(), entry.getValue());
		}
		return result;
	}

	private static UUID getUuidFromString(String data) {
		String md5Hex = DigestUtils.md5Hex(data);
		md5Hex = md5Hex.replaceFirst(regex, replacement);
		return UUID.fromString(md5Hex);
	}

	private static SimpleObject removeFields(SimpleObject simpleObject, Class<?> clazz) {
		SimpleObject result = copySimpleObject(simpleObject);
		result.remove("dateChanged");
		removeVoided(result, clazz);
		removeId(result, clazz);
		return result;
	}

	private static void removeVoided(final SimpleObject result, Class<?> clazz) {
		if (BaseOpenmrsData.class.isAssignableFrom(clazz)) {
			result.remove("dateVoided");
			result.remove("voided");
			result.remove("voidedBy");
			result.remove("voidReason");
		} else if (BaseOpenmrsMetadata.class.isAssignableFrom(clazz)) {
			result.remove("dateRetired");
			result.remove("retired");
			result.remove("retiredBy");
			result.remove("retireReason");
		}
	}

	private static void removeId(final SimpleObject result, Class<?> clazz) {
		String idName;
		if (Location.class.isAssignableFrom(clazz)) {
			idName = LOCATION_ID;
		} else if (Obs.class.isAssignableFrom(clazz)) {
			idName = OBS_ID;
		} else if (Encounter.class.isAssignableFrom(clazz)) {
			idName = ENCOUNTER_ID;
		} else if (Visit.class.isAssignableFrom(clazz)) {
			idName = VISIT_ID;
		} else if (Provider.class.isAssignableFrom(clazz)) {
			idName = PROVIDER_ID;
		} else if (Drug.class.isAssignableFrom(clazz)) {
			idName = DRUG_ID;
		} else if (Form.class.isAssignableFrom(clazz)) {
			idName = FORM_ID;
		} else if (Order.class.isAssignableFrom(clazz)) {
			idName = ORDER_ID;
		} else if (PatientProgram.class.isAssignableFrom(clazz)) {
			idName = PATIENT_PROGRAM_ID;
		} else if (Person.class.isAssignableFrom(clazz)) {
			idName = PERSON_ID;
		} else if (Patient.class.isAssignableFrom(clazz)) {
			idName = PATIENT_ID;
		} else if (Relationship.class.isAssignableFrom(clazz)) {
			idName = RELATIONSHIP_ID;
		} else if (Cohort.class.isAssignableFrom(clazz)) {
			idName = COHORT_ID;
		} else if (VisitType.class.isAssignableFrom(clazz)) {
			idName = VISIT_TYPE_ID;
		} else if (User.class.isAssignableFrom(clazz)) {
			idName = USER_ID;
		} else if (Program.class.isAssignableFrom(clazz)) {
			idName = PROGRAM_ID;
		} else if (Privilege.class.isAssignableFrom(clazz)) {
			idName = null;
		} else if (AuditMessage.class.isAssignableFrom(clazz)) {
			idName = AUDIT_MESSAGE_ID;
		} else {
			idName = null;
		}

		if (idName != null) {
			result.remove(idName);
		}
	}
}
