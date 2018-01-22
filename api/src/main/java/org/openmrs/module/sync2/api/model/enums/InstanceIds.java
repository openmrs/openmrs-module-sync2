package org.openmrs.module.sync2.api.model.enums;

public enum InstanceIds {

    ALL("");

    private String regex;

    InstanceIds(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
