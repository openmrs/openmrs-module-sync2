package org.openmrs.module.sync2.api.model.configuration;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Objects;

public class GeneralConfiguration implements Serializable {

    private static final long serialVersionUID = -2762773054306474129L;

    private String parentFeedLocation;

    private String localFeedLocation;

    private String localInstanceId;

    private boolean persistSuccessAudit;

    private boolean persistFailureAudit;

    private LinkedHashMap<String, ClientConfiguration> clients;

    public GeneralConfiguration() {
        clients = new LinkedHashMap<>();
    }

    public GeneralConfiguration(String parentFeedLocation, String localFeedLocation, String localInstanceId,
                                boolean persistSuccessAudit, boolean persistFailureAudit,
            LinkedHashMap<String, ClientConfiguration> clients) {
        this.parentFeedLocation = parentFeedLocation;
        this.localFeedLocation = localFeedLocation;
        this.localInstanceId = localInstanceId;
        this.persistSuccessAudit = persistSuccessAudit;
        this.persistFailureAudit = persistFailureAudit;
        this.clients = clients;
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

    public LinkedHashMap<String, ClientConfiguration> getClients() {
        return clients;
    }

    public void setClients(LinkedHashMap<String, ClientConfiguration> clients) {
        this.clients = clients;
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
                && Objects.equals(persistFailureAudit, that.persistFailureAudit)
                && Objects.deepEquals(clients, that.clients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFeedLocation, localFeedLocation, localInstanceId,
                persistSuccessAudit, persistFailureAudit, clients);
    }
}
