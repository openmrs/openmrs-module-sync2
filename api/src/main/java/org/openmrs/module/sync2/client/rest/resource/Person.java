package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.Concept;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
//    @Expose
//    private Concept causeOfDeath;
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

//    public Concept getCauseOfDeath() {
//        return causeOfDeath;
//    }

//    public void setCauseOfDeath(Concept causeOfDeath) {
//        this.causeOfDeath = causeOfDeath;
//    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;
        return Objects.equals(uuid, person.uuid) &&
                Objects.equals(display, person.display) &&
                Objects.equals(preferredName, person.preferredName) &&
                Objects.equals(preferredAddress, person.preferredAddress) &&
                Objects.equals(names, person.names) &&
                Objects.equals(addresses, person.addresses) &&
                Objects.equals(birthdateEstimated, person.birthdateEstimated) &&
                Objects.equals(age, person.age) &&
                Objects.equals(gender, person.gender) &&
                Objects.equals(dead, person.dead) &&
//                Objects.equals(causeOfDeath, person.causeOfDeath) &&
                Objects.equals(deathDate, person.deathDate) &&
                Objects.equals(voided, person.voided) &&
                Objects.equals(deathdateEstimated, person.deathdateEstimated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, display, preferredName, preferredAddress, names, addresses, birthdateEstimated, age, gender, dead, deathDate, voided, deathdateEstimated);
    }

}
