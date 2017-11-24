package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsObject;

import java.util.List;

public class PersonName implements RestResource {
    private String display;
    private String uuid;
    private String givenName;
    private String middleName;
    private String familyName;
    private String familyName2;
    private Boolean voided;
    private List<Link> links;
    private String resourceVersion;

    // region getters
    public String getDisplay() {
        return display;
    }

    public String getUuid() {
        return uuid;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFamilyName2() {
        return familyName2;
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
    public void setDisplay(String display) {
        this.display = display;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setFamilyName2(String familyName2) {
        this.familyName2 = familyName2;
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
        org.openmrs.PersonName personName = new org.openmrs.PersonName();
        personName.setUuid(uuid);
        personName.setGivenName(givenName);
        personName.setMiddleName(middleName);
        personName.setFamilyName(familyName);
        personName.setFamilyName2(familyName2);
        personName.setVoided(voided);
        return personName;
    }
}
