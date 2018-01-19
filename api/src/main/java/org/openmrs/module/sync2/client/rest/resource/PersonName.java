package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.BaseOpenmrsObject;

import java.util.List;
import java.util.Objects;

public class PersonName implements RestResource {
    private String uuid;
    private String display;

    @Expose
    private String givenName;
    @Expose
    private String middleName;
    @Expose
    private String familyName;
    @Expose
    private String familyName2;
    @Expose
    private Boolean voided;
    @Expose
    private List<Link> links;
    @Expose
    private String resourceVersion;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyName2() {
        return familyName2;
    }

    public void setFamilyName2(String familyName2) {
        this.familyName2 = familyName2;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonName that = (PersonName) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(display, that.display) &&
                Objects.equals(givenName, that.givenName) &&
                Objects.equals(middleName, that.middleName) &&
                Objects.equals(familyName, that.familyName) &&
                Objects.equals(familyName2, that.familyName2) &&
                Objects.equals(voided, that.voided);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, display, givenName, middleName, familyName, familyName2, voided);
    }

}
