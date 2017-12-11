package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.Concept;

import java.util.Date;
import java.util.List;

/**
 * Order of fields is taken from webservices
 */
public class Person {
    private String uuid;
    private String display;
    private PersonName preferredName;
    private Address preferredAddress;

    @Expose
    private List<PersonName> names;
    @Expose
    private List<Address> addresses;
    @Expose
    private Date birthdate;
    @Expose
    private Boolean birthdateEstimated;
    @Expose
    private Integer age;
    @Expose
    private String gender;
    @Expose
    private Boolean dead;
    @Expose
    private Concept causeOfDeath;
    @Expose
    private Date deathDate;
    @Expose
    private Boolean voided;
    @Expose
    private Boolean deathdateEstimated;
    @Expose
    private Date birthtime;

    public Person() {
    }

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

    public PersonName getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(PersonName preferredName) {
        this.preferredName = preferredName;
    }

    public List<PersonName> getNames() {
        return names;
    }

    public void setNames(List<PersonName> names) {
        this.names = names;
    }

    public Address getPreferredAddress() {
        return preferredAddress;
    }

    public void setPreferredAddress(Address preferredAddress) {
        this.preferredAddress = preferredAddress;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public Concept getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(Concept causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Boolean getDeathdateEstimated() {
        return deathdateEstimated;
    }

    public void setDeathdateEstimated(Boolean deathdateEstimated) {
        this.deathdateEstimated = deathdateEstimated;
    }

    public Date getBirthtime() {
        return birthtime;
    }

    public void setBirthtime(Date birthtime) {
        this.birthtime = birthtime;
    }
}
