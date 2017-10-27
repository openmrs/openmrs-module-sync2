package org.openmrs.module.sync2.api.exceptions;

public class Sync2Exception extends RuntimeException {

    public Sync2Exception(String message) {
        super(message);
    }

    public Sync2Exception(Throwable cause) {
        super(cause);
    }

    public Sync2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
