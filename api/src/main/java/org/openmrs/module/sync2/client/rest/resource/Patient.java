package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Patient implements RestResource {
    private String uuid;
    private String display;
    private Set<Identifier> identifiers;
    private Person person;

    public String getUuid() {
        return uuid;
    }

    public String getDisplay() {
        return display;
    }

    public Set<Identifier> getIdentifiers() {
        return identifiers;
    }

    public Person getPerson() {
        return person;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setIdentifiers(Set<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Converts resource Patient to org.openmrs.Patient
     * @return
     */
    @Override
    public org.openmrs.BaseOpenmrsObject getOpenMrsObject() {
        org.openmrs.Patient patient = new org.openmrs.Patient();
        patient.setUuid(uuid);

        Set<PatientIdentifier> patientIdentifierList = new HashSet<>();
        for (Identifier identifier : identifiers) {
            patientIdentifierList.add((PatientIdentifier) identifier.getOpenMrsObject());
        }
        patient.setIdentifiers(patientIdentifierList);
        patient.setGender(person.getGender());
        patient.setBirthdate(person.getBirthdate());
        patient.setDead(person.getDead());
        patient.setDeathDate(person.getDeathDate());
        patient.setCauseOfDeath(person.getCauseOfDeath());
        patient.setNames(Collections.singleton(
                (PersonName) person.getPreferredName().getOpenMrsObject()));
        patient.setVoided(person.getVoided());
        patient.setDeathdateEstimated(person.getDeathdateEstimated());
        patient.setBirthtime(person.getBirthtime());

        return patient;
    }
}
