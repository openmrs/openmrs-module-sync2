package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Patient implements RestResource {

    private String uuid;
    private String display;

    @Expose
    private List<Identifier> identifiers = new ArrayList<Identifier>();
    @Expose
    private Person person;

    public Patient() {
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

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Person getPerson() {
        return person;
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

        Set<PatientIdentifier> patientIdentifierList = new TreeSet<>();
        for (Identifier identifier : identifiers) {
            patientIdentifierList.add((PatientIdentifier) identifier.getOpenMrsObject());
        }
        patient.setIdentifiers(patientIdentifierList);
        patient.setGender(person.getGender());
        patient.setBirthdate(person.getBirthdate());
        patient.setDead(person.getDead());
        patient.setDeathDate(person.getDeathDate());
        patient.setCauseOfDeath(person.getCauseOfDeath());

        Set<PersonName> personNameSet = new TreeSet<>();
        PersonName preferredName = null;
        if (person.getPreferredName() != null) {
            preferredName = (PersonName) person.getPreferredName().getOpenMrsObject();
        }
        for (org.openmrs.module.sync2.client.rest.resource.PersonName name : person.getNames()) {
            PersonName openmrsName = (PersonName)name.getOpenMrsObject();
            if (preferredName != null && preferredName.equalsContent(openmrsName)) {
                openmrsName.setPreferred(true);
            }
            personNameSet.add(openmrsName);
        }
        patient.setNames(personNameSet);

        Set<PersonAddress> personAddressesSet = new TreeSet<>();
        PersonAddress preferredAddress = null;
        if (person.getPreferredAddress() != null) {
            preferredAddress = (PersonAddress) person.getPreferredAddress().getOpenMrsObject();
        }
        for (Address address : person.getAddresses()) {
            PersonAddress openmrsAddress = (PersonAddress) address.getOpenMrsObject();
            if (preferredAddress != null && preferredAddress.equalsContent(openmrsAddress)) {
                openmrsAddress.setPreferred(true);
            }
            personAddressesSet.add(openmrsAddress);
        }
        patient.setAddresses(personAddressesSet);

        patient.setVoided(person.getVoided());
        patient.setDeathdateEstimated(person.getDeathdateEstimated());
        patient.setBirthtime(person.getBirthtime());

        return patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Patient patient = (Patient) o;
        return Objects.equals(uuid, patient.uuid) &&
                Objects.equals(display, patient.display) &&
                Objects.equals(identifiers, patient.identifiers) &&
                Objects.equals(person, patient.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, display, identifiers, person);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "uuid='" + uuid + '\'' +
                ", display='" + display + '\'' +
                ", identifiers=" + identifiers +
                ", person=" + person +
                '}';
    }
}
