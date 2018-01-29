package org.openmrs.module.sync2.api.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Errors {

    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getErrors(String error) {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        return "Errors{" +
                "errors=" + errors +
                '}';
    }
}
