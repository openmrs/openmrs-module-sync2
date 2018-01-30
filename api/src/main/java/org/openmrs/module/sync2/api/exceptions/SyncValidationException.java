package org.openmrs.module.sync2.api.exceptions;

import org.openmrs.module.sync2.api.validator.Errors;

public class SyncValidationException extends SyncException {

    private Errors errors;

    public SyncValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public SyncValidationException(Throwable cause, Errors errors) {
        super(cause);
        this.errors = errors;
    }

    public SyncValidationException(String message, Throwable cause, Errors errors) {
        super(message, cause);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
