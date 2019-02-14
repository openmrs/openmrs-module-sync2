package org.openmrs.module.sync2.api.model.enums;

import org.openmrs.module.sync2.SyncCategoryConstants;

public enum Resources {

    ALL(SyncCategoryConstants.ALL, SyncCategoryConstants.ALL_LABEL),
    PATIENT(SyncCategoryConstants.CATEGORY_PATIENT, SyncCategoryConstants.PATIENT_LABEL),
    VISIT(SyncCategoryConstants.CATEGORY_VISIT, SyncCategoryConstants.VISIT_LABEL),
    ENCOUNTER(SyncCategoryConstants.CATEGORY_ENCOUNTER, SyncCategoryConstants.ENCOUNTER_LABEL),
    OBSERVATION(SyncCategoryConstants.CATEGORY_OBSERVATION, SyncCategoryConstants.OBSERVATION_LABEL),
    LOCATION(SyncCategoryConstants.CATEGORY_LOCATION, SyncCategoryConstants.LOCATION_LABEL),
    PRIVILEGE(SyncCategoryConstants.CATEGORY_PRIVILEGE, SyncCategoryConstants.PRIVILEGE_LABEL),
    AUDIT_MESSAGE(SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE, SyncCategoryConstants.AUDIT_MESSAGE_LABEL),
    PERSON(SyncCategoryConstants.CATEGORY_PERSON, SyncCategoryConstants.PERSON_LABEL),
    ALLERGY(SyncCategoryConstants.CATEGORY_ALLERGY, SyncCategoryConstants.ALLERGY_LABEL),
    FORM(SyncCategoryConstants.CATEGORY_FORM, SyncCategoryConstants.FORM_LABEL),
    COHORT(SyncCategoryConstants.CATEGORY_COHORT, SyncCategoryConstants.COHORT_LABEL),
    DRUG_ORDER(SyncCategoryConstants.CATEGORY_DRUG_ORDER, SyncCategoryConstants.DRUG_ORDER_LABEL),
    TEST_ORDER(SyncCategoryConstants.CATEGORY_TEST_ORDER, SyncCategoryConstants.TEST_ORDER_LABEL),
    PERSON_ADDRESS(SyncCategoryConstants.CATEGORY_PERSON_ADDRESS, SyncCategoryConstants.PERSON_ADDRESS_LABEL),
    PROVIDER(SyncCategoryConstants.CATEGORY_PROVIDER, SyncCategoryConstants.PROVIDER_LABEL),
    PATIENT_PROGRAM(SyncCategoryConstants.CATEGORY_PATIENT_PROGRAM, SyncCategoryConstants.PATIENT_PROGRAM_LABEL),
    RELATIONSHIP(SyncCategoryConstants.CATEGORY_RELATIONSHIP, SyncCategoryConstants.RELATIONSHIP_LABEL),
    VISIT_TYPE(SyncCategoryConstants.CATEGORY_VISIT_TYPE, SyncCategoryConstants.VISIT_TYPE_LABEL),
    USER(SyncCategoryConstants.CATEGORY_USER, SyncCategoryConstants.USER_LABEL),
    PROGRAM(SyncCategoryConstants.CATEGORY_PROGRAM, SyncCategoryConstants.PROGRAM_LABEL),
    PERSON_NAME(SyncCategoryConstants.CATEGORY_PERSON_NAME, SyncCategoryConstants.PERSON_NAME_LABEL),
    PATIENT_IDENTIFIER(SyncCategoryConstants.CATEGORY_PATIENT_IDENTIFIER, SyncCategoryConstants.PATIENT_IDENTIFIER_LABEL);

    private final String name;

    private final String messageKey;

    Resources(String name, String messageKey) {
        this.name = name;
        this.messageKey = messageKey;
    }

    public String getName() {
        return this.name;
    }

    public String getMessageKey() {
        return messageKey;
    }

    private static class Constants {


    }
}
