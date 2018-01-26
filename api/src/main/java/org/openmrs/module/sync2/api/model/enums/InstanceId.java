package org.openmrs.module.sync2.api.model.enums;

public enum InstanceId {

    ALL("");

    private String regex;

    InstanceId(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
