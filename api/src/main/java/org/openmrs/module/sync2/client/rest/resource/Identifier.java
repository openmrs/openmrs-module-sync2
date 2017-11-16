package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsData;

import java.util.List;

public class Identifier implements RestResource {
    private String display;
    private String uuid;
    private String identifier;
    /**
     * field identifierType omitted because it's Metadata not Data
     *
     * field location omitted because it's Metadata not Data
     */
    private Boolean preferred;
    private Boolean voided;

    private List<Link> links;
    private String resourceVersion;

    public String getUuid() {
        return uuid;
    }

    public String getDisplay() {
        return display;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public Boolean getVoided() {
        return voided;
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Override
    public BaseOpenmrsData getOpenMrsObject() {
        org.openmrs.PatientIdentifier patientIdentifier = new org.openmrs.PatientIdentifier();
        patientIdentifier.setUuid(uuid);
        patientIdentifier.setIdentifier(identifier);
        patientIdentifier.setPreferred(preferred);
        patientIdentifier.setVoided(voided);

        return patientIdentifier;
    }
}
