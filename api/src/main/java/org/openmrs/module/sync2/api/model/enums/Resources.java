package org.openmrs.module.sync2.api.model.enums;

public enum Resources {

    ALL(""),
    PATIENT("patient"),
    VISIT("visit"),
    ENCOUNTER("encounter"),
    OB("ob"),
    LOCATION("location"),
    PRIVILEGE("privilege"),
    AUDIT_MESSAGE("audit_message"),
    ALLERGY("allergy");

    private final String name;

    Resources(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
