package org.openmrs.module.sync2.client;

import org.openmrs.OpenmrsObject;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.module.sync2.client.rest.resource.Address;
import org.openmrs.module.sync2.client.rest.resource.Identifier;
import org.openmrs.module.sync2.client.rest.resource.IdentifierType;
import org.openmrs.module.sync2.client.rest.resource.Location;
import org.openmrs.module.sync2.client.rest.resource.LocationTag;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Person;
import org.openmrs.module.sync2.client.rest.resource.PersonName;
import org.openmrs.module.sync2.client.rest.resource.Privilege;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RestResourceCreationUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestResourceCreationUtil.class);

    public static RestResource createRestResourceFromOpenMRSData(OpenmrsObject object) {
        if (object instanceof org.openmrs.Patient) {
            return createPatientFromOpenMRSPatient((org.openmrs.Patient) object);
        } else if (object instanceof org.openmrs.Location) {
            return createLocationFromOpenMRSLocation((org.openmrs.Location) object, false);
        } else if (object instanceof org.openmrs.Privilege) {
            return createPrivilegeFromOpenMrsPrivilege((org.openmrs.Privilege) object);
        }

        LOGGER.warn(String.format("Unrecognized openmrs object type %s", object.getClass().getSimpleName()));
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

    private static RestResource createPrivilegeFromOpenMrsPrivilege(org.openmrs.Privilege openMrsPrivilege) {
        Privilege privilege = new Privilege();
        privilege.setName(openMrsPrivilege.getPrivilege());
        privilege.setDescription(openMrsPrivilege.getDescription());
        return privilege;
    }

    private static Identifier createIdentifierFromOpenMRSPatientIdentifier(PatientIdentifier patientIdentifier) {
        Identifier identifier = new Identifier();

        identifier.setUuid(patientIdentifier.getUuid());
        identifier.setIdentifier(patientIdentifier.getIdentifier());
        if (patientIdentifier.getIdentifierType() != null) {
            identifier.setIdentifierType(createIdentifierTypeFromOpenMRSPatientIdentifierType(patientIdentifier.getIdentifierType()));
        }
        if (patientIdentifier.getLocation() != null) {
            identifier.setLocation(createLocationFromOpenMRSLocation(patientIdentifier.getLocation(), true));
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

    private static Location createLocationFromOpenMRSLocation(org.openmrs.Location openMRSLocation, boolean reference) {
        Location location = new Location();
        location.setName(openMRSLocation.getName());
        if (reference) {
            location.setUuid(openMRSLocation.getUuid());
        } else {
            location.setDescription(openMRSLocation.getDescription());
            location.setCityVillage(openMRSLocation.getCityVillage());
            location.setStateProvince(openMRSLocation.getStateProvince());
            location.setCountry(openMRSLocation.getCountry());
            location.setPostalCode(openMRSLocation.getPostalCode());
            location.setLatitude(openMRSLocation.getLatitude());
            location.setLongitude(openMRSLocation.getLongitude());
            location.setCountryDistrict(openMRSLocation.getCountyDistrict());
            List<LocationTag> locationTagList = new ArrayList<>();
            for (org.openmrs.LocationTag omrsLocationTag : openMRSLocation.getTags()) {
                locationTagList.add(createLocationTagFromOpenMrsLocationTag(omrsLocationTag, true));
            }
            location.setTags(locationTagList);
            location.setAddress1(openMRSLocation.getAddress1());
            location.setAddress2(openMRSLocation.getAddress2());
            location.setAddress3(openMRSLocation.getAddress3());
            location.setAddress4(openMRSLocation.getAddress4());
            location.setAddress5(openMRSLocation.getAddress5());
            location.setAddress6(openMRSLocation.getAddress6());
            if (openMRSLocation.getParentLocation() != null) {
                location.setParentLocationRef(openMRSLocation.getParentLocation().getName());
            }
            location.setRetired(openMRSLocation.getRetired());
        }
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

    private static LocationTag createLocationTagFromOpenMrsLocationTag(org.openmrs.LocationTag omrsLocationTag,
                                                                       Boolean reference) {
        LocationTag locationTag = new LocationTag();
        locationTag.setUuid(omrsLocationTag.getUuid());
        if (!reference) {
            locationTag.setName(omrsLocationTag.getName());
            locationTag.setDescription(omrsLocationTag.getDescription());
            locationTag.setRetired(omrsLocationTag.getRetired());
        }
        return locationTag;
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
