package org.openmrs.module.sync2.api.model.enums;

import org.openmrs.module.sync2.SyncConstants;

public enum SyncOperation {
    ALL(SyncConstants.ALL_OPERATIONS), PUSH(SyncConstants.PUSH_OPERATION), PULL(SyncConstants.PULL_OPERATION);

    private String value;

    SyncOperation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SyncOperation getByValue(String value) {
        SyncOperation result = SyncOperation.ALL;
        for (SyncOperation operation : SyncOperation.values()) {
            if (operation.getValue().equalsIgnoreCase(value)) {
                result = operation;
                break;
            }
        }
        return result;
    }
}
