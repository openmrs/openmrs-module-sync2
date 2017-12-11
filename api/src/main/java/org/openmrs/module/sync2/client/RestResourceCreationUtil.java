package org.openmrs.module.sync2.client;

import org.openmrs.OpenmrsData;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.module.sync2.client.rest.resource.Address;
import org.openmrs.module.sync2.client.rest.resource.Identifier;
import org.openmrs.module.sync2.client.rest.resource.IdentifierType;
import org.openmrs.module.sync2.client.rest.resource.Location;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Person;
import org.openmrs.module.sync2.client.rest.resource.PersonName;
import org.openmrs.module.sync2.client.rest.resource.RestResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RestResourceCreationUtil {

    public static RestResource createRestResourceFromOpenMRSData(OpenmrsData object) {
        if (object instanceof org.openmrs.Patient) {
            return createPatientFromOpenMRSPatient((org.openmrs.Patient) object);
        }
        return null;
    }

    private static Patient createPatientFromOpenMRSPatient(org.openmrs.Patient openMRSPatient) {
        Patient patient = new Patient();

        List<Identifier> identifiers = new ArrayList<>();
        for (PatientIdentifier patientIdentifier : openMRSPatient.getIdentifiers()) {
            identifiers.add(createIdentifierFromOpenMRSPatientIdentifier(patientIdentifier));
        }
        patient.setIdentifiers(identifiers);

        patient.setPerson(createPersonFromOpenMRSPerson(openMRSPatient.getPerson()));

        return patient;
    }

    private static Identifier createIdentifierFromOpenMRSPatientIdentifier(PatientIdentifier patientIdentifier) {
        Identifier identifier = new Identifier();

        identifier.setUuid(patientIdentifier.getUuid());
        identifier.setIdentifier(patientIdentifier.getIdentifier());
        if (patientIdentifier.getIdentifierType() != null) {
            identifier.setIdentifierType(createIdentifierTypeFromOpenMRSPatientIdentifierType(patientIdentifier.getIdentifierType()));
        }
        if (patientIdentifier.getLocation() != null) {
            identifier.setLocation(createLocationFromOpenMRSLocation(patientIdentifier.getLocation()));
        }
        identifier.setPreferred(patientIdentifier.getPreferred());
        identifier.setVoided(patientIdentifier.getVoided());

        return identifier;
    }

    private static IdentifierType createIdentifierTypeFromOpenMRSPatientIdentifierType(PatientIdentifierType patientIdentifierType) {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setName(patientIdentifierType.getName());
        identifierType.setDescription(patientIdentifierType.getDescription());
        return identifierType;
    }

    private static Location createLocationFromOpenMRSLocation(org.openmrs.Location openMRSLocation) {
        Location location = new Location();
        location.setUuid(openMRSLocation.getUuid());
        return location;
    }

    private static List<PersonName> createNamesFromOpenMRSPersonName(org.openmrs.PersonName openMRSPersonName) {
        PersonName personName = new PersonName();
        personName.setGivenName(openMRSPersonName.getGivenName());
        personName.setMiddleName(openMRSPersonName.getMiddleName());
        personName.setFamilyName(openMRSPersonName.getFamilyName());
        personName.setFamilyName2(openMRSPersonName.getFamilyName2());

        List<PersonName> names = new ArrayList<>();
        names.add(personName);
        return names;
    }

    private static Person createPersonFromOpenMRSPerson(org.openmrs.Person openMRSPerson) {
        Person person = new Person();
        person.setAddresses(createAddressFromOpenMRSAddress(openMRSPerson.getAddresses()));
        person.setUuid(openMRSPerson.getUuid());
        person.setGender(openMRSPerson.getGender());
        person.setBirthdate(openMRSPerson.getBirthdate());
        person.setDead(openMRSPerson.getDead());
        person.setDeathDate(openMRSPerson.getDeathDate());
        person.setCauseOfDeath(openMRSPerson.getCauseOfDeath());
        person.setNames(createNamesFromOpenMRSPersonName(openMRSPerson.getPersonName()));
        person.setVoided(openMRSPerson.getVoided());
        person.setDeathdateEstimated(openMRSPerson.getDeathdateEstimated());
        person.setBirthtime(openMRSPerson.getBirthtime());

        return person;
    }

    private static List<Address> createAddressFromOpenMRSAddress(Set<PersonAddress> personAddresses) {
        List<Address> addresses = new ArrayList<>();

        for (PersonAddress address : personAddresses) {
            addresses.add(new Address.Builder()
                    .setUuid(address.getUuid())
                    .setAddress1(address.getAddress1())
                    .setAddress2(address.getAddress2())
                    .setAddress3(address.getAddress3())
                    .setAddress4(address.getAddress4())
                    .setAddress5(address.getAddress5())
                    .setAddress6(address.getAddress6())
                    .setCityVillage(address.getCityVillage())
                    .setCountry(address.getCountry())
                    .setCountyDistrict(address.getCountyDistrict())
                    .setEndDate(address.getEndDate())
                    .setLatitude(address.getLatitude())
                    .setLongitude(address.getLongitude())
                    .setPostalCode(address.getPostalCode())
                    .setStartDate(address.getStartDate())
                    .setStateProvince(address.getStateProvince())
                    .create());
        }

        return addresses;
    }
}
