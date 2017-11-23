package org.openmrs.module.sync2.api.exceptions;

public class SyncException extends RuntimeException {

    public SyncException(String message) {
        super(message);
    }

    public SyncException(Throwable cause) {
        super(cause);
    }

    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
