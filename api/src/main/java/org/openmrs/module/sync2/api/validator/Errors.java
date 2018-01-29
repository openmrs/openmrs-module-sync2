package org.openmrs.module.sync2.api.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Errors {

    private List<String> errorCodes = new ArrayList<>();

    public void addErrorCode(String errorCode) {
        errorCodes.add(errorCode);
    }

    public List<String> getErrorsCodes() {
        return Collections.unmodifiableList(errorCodes);
    }

    public boolean hasErrors() {
        return !errorCodes.isEmpty();
    }

    @Override
    public String toString() {
        return "Errors{" +
                "errorCodes=" + errorCodes +
                '}';
    }
}
