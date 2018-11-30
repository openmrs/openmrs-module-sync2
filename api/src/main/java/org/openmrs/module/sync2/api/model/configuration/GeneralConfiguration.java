package org.openmrs.module.sync2.api.model.configuration;

import java.io.Serializable;
import java.util.Objects;

public class GeneralConfiguration implements Serializable {

    private static final long serialVersionUID = -2762773054306474129L;

    private String parentFeedLocation;
    private String localFeedLocation;
    private String localInstanceId;
    private boolean persistSuccessAudit;
    private boolean persistFailureAudit;

    public GeneralConfiguration() { }

    public GeneralConfiguration(String parentFeedLocation, String localFeedLocation, String localInstanceId,
                                boolean persistSuccessAudit, boolean persistFailureAudit) {
        this.parentFeedLocation = parentFeedLocation;
        this.localFeedLocation = localFeedLocation;
        this.localInstanceId = localInstanceId;
        this.persistSuccessAudit = persistSuccessAudit;
        this.persistFailureAudit = persistFailureAudit;
    }

    public String getParentFeedLocation() {
        return parentFeedLocation;
    }

    public void setParentFeedLocation(String parentFeedLocation) {
        this.parentFeedLocation = parentFeedLocation;
    }

    public String getLocalFeedLocation() {
        return localFeedLocation;
    }

    public void setLocalFeedLocation(String localFeedLocation) {
        this.localFeedLocation = localFeedLocation;
    }

    public boolean isPersistSuccessAudit() {
        return persistSuccessAudit;
    }

    public void setPersistSuccessAudit(boolean persistSuccessAudit) {
        this.persistSuccessAudit = persistSuccessAudit;
    }

    public boolean isPersistFailureAudit() {
        return persistFailureAudit;
    }

    public void setPersistFailureAudit(boolean persistFailureAudit) {
        this.persistFailureAudit = persistFailureAudit;
    }
    
    public String getLocalInstanceId() {
        return localInstanceId;
    }
    
    public void setLocalInstanceId(String localInstanceId) {
        this.localInstanceId = localInstanceId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneralConfiguration that = (GeneralConfiguration) o;
        return Objects.equals(parentFeedLocation, that.parentFeedLocation)
                && Objects.equals(localFeedLocation, that.localFeedLocation)
                && Objects.equals(localInstanceId, that.localInstanceId)
                && Objects.equals(persistSuccessAudit, that.persistSuccessAudit)
                && Objects.equals(persistFailureAudit, that.persistFailureAudit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFeedLocation, localFeedLocation, localInstanceId,
                persistSuccessAudit, persistFailureAudit);
    }
}
