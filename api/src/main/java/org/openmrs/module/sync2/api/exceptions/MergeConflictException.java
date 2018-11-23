package org.openmrs.module.sync2.api.exceptions;

public class MergeConflictException extends Exception {

	public MergeConflictException(String message) {
		super(message);
	}

	public MergeConflictException(Throwable cause) {
		super(cause);
	}

	public MergeConflictException(String message, Throwable cause) {
		super(message, cause);
	}
}
