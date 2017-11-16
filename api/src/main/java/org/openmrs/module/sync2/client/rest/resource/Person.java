package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.Concept;

import java.util.Date;

/**
 * Order of fields is taken from webservices
 */
public class Person {
    private String uuid;
    private String display;
    private String gender;
    private int age;
    private Date birthdate;
    private Boolean birthdateEstimated;
    private Boolean dead;
    private Date deathDate;
    private Concept causeOfDeath;
    private PersonName preferredName;
    /**
     * [...]
     */
    private Boolean voided;
    private Boolean deathdateEstimated;
    private Date birthtime;

    // getters
    public String getUuid() {
        return uuid;
    }

    public String getDisplay() {
        return display;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public Date getBirthdate() {
        if (birthdate == null) {
            return null;
        }
        return new Date(birthdate.getTime());
    }

    public Boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    public Boolean getDead() {
        return dead;
    }

    public Date getDeathDate() {
        if (deathDate == null) {
            return null;
        }
        return new Date(deathDate.getTime());
    }

    public Concept getCauseOfDeath() {
        return causeOfDeath;
    }

    public PersonName getPreferredName() {
        return preferredName;
    }

    public Boolean getVoided() {
        return voided;
    }

    public Boolean getDeathdateEstimated() {
        return deathdateEstimated;
    }

    public Date getBirthtime() {
        if (birthtime == null) {
            return null;
        }
        return new Date(birthtime.getTime());
    }

    // setters
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBirthdate(Date birthdate) {
        if (birthdate == null) {
            this.birthdate = null;
        } else {
            this.birthdate = new Date(birthdate.getTime());
        }
    }

    public void setBirthdateEstimated(Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public void setDeathDate(Date deathDate) {
        if (deathDate == null) {
            this.deathDate = null;
        } else {
            this.deathDate = new Date(deathDate.getTime());
        }
    }

    public void setCauseOfDeath(Concept causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public void setPreferredName(PersonName preferredName) {
        this.preferredName = preferredName;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public void setDeathdateEstimated(Boolean deathdateEstimated) {
        this.deathdateEstimated = deathdateEstimated;
    }

    public void setBirthtime(Date birthtime) {
        if (birthtime == null) {
            this.birthtime = null;
        } else {
            this.birthtime = new Date(birthtime.getTime());
        }
    }
}
