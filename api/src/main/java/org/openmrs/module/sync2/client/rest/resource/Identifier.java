package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.PatientIdentifierType;

import java.util.List;
import java.util.Objects;

public class Identifier implements RestResource {
    private String display;
    private String uuid;
    private String identifier;
    private IdentifierType identifierType;
    private Location location;
    private Boolean preferred;
    private Boolean voided;

    private List<Link> links;
    private String resourceVersion;


    // region getters
    public String getUuid() {
        return uuid;
    }

    public String getDisplay() {
        return display;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public Location getLocation() {
        return location;
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
    // endregion

    // region setters
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setIdentifierType(IdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public void setLocation(Location location) {
        this.location = location;
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
    // endregion

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        org.openmrs.PatientIdentifier patientIdentifier = new org.openmrs.PatientIdentifier();
        patientIdentifier.setUuid(uuid);
        patientIdentifier.setIdentifier(identifier);
        if (identifierType != null) {
            patientIdentifier.setIdentifierType((PatientIdentifierType) identifierType.getOpenMrsObject());
        }
        if (location != null) {
            patientIdentifier.setLocation((org.openmrs.Location) location.getOpenMrsObject());
        }
        patientIdentifier.setPreferred(preferred);
        patientIdentifier.setVoided(voided);

        return patientIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Identifier that = (Identifier) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(identifierType, that.identifierType) &&
                Objects.equals(location, that.location) &&
                Objects.equals(preferred, that.preferred) &&
                Objects.equals(voided, that.voided);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, identifier, identifierType, location, preferred, voided);
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "display='" + display + '\'' +
                ", uuid='" + uuid + '\'' +
                ", identifier='" + identifier + '\'' +
                ", identifierType=" + identifierType +
                ", location=" + location +
                ", preferred=" + preferred +
                ", voided=" + voided +
                ", links=" + links +
                ", resourceVersion='" + resourceVersion + '\'' +
                '}';
    }
}
