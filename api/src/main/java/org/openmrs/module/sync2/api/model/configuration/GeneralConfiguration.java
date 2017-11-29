package org.openmrs.module.sync2.api.model.configuration;

import java.util.Objects;

public class GeneralConfiguration {

    private String parentFeedLocation;
    private String localFeedLocation;
    private boolean persistSuccessAudit;
    private boolean persistFailureAudit;

    public GeneralConfiguration() { }

    public GeneralConfiguration(String parentFeedLocation, String localFeedLocation, boolean persistSuccessAudit,
                                boolean persistFailureAudit) {
        this.parentFeedLocation = parentFeedLocation;
        this.localFeedLocation = localFeedLocation;
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
                && Objects.equals(persistSuccessAudit, that.persistSuccessAudit)
                && Objects.equals(persistFailureAudit, that.persistFailureAudit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFeedLocation, localFeedLocation, persistSuccessAudit, persistFailureAudit);
    }
}
