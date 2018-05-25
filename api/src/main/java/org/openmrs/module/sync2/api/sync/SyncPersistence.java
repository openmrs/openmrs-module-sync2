package org.openmrs.module.sync2.api.sync;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.encounter.EncounterStrategyUtil;
import org.openmrs.module.fhir.api.strategies.location.LocationStrategyUtil;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;
import org.openmrs.module.fhir.api.strategies.visit.VisitStrategyUtil;
import org.openmrs.module.fhir.api.strategies.observation.ObservationStrategyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;

public class SyncPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPersistence.class);

    private static final String CATEGORY_PATIENT = "patient";
    private static final String CATEGORY_VISIT = "visit";
    private static final String CATEGORY_ENCOUNTER = "encounter";
    private static final String CATEGORY_OB = "ob";
    private static final String CATEGORY_LOCATION = "location";
    private static final String CATEGORY_PRIVILEGE = "privilege";
    
    private static final String VOIDING_REASON = "Voided by Sync 2";
    
    public void persistRetrievedData(Object retrievedObject, String action) {
        if (retrievedObject instanceof OpenmrsObject) {
            LOGGER.info("persistRetrievedData openmrsObject");
            persistRetrievedRestData(retrievedObject, action);
        } else if (retrievedObject instanceof DomainResource) {
            LOGGER.info("persistRetrievedData domainResource");
            persistRetrievedFhirData(retrievedObject, action);
        } else {
            LOGGER.info("persistRetrievedData unrecognized object");
            LOGGER.warn(String.format("Unrecognized object type %s", retrievedObject.getClass().getSimpleName()));
        }
    }

    public Object retrieveData(String client, String category, String uuid) {
        switch (client) {
            case FHIR_CLIENT:
                return retrieveFhirObject(category, uuid);
            case REST_CLIENT:
                return retrieveRestObject(category, uuid);
            default:
                LOGGER.warn(String.format("Unrecognized client %s, falling back to core OpenMrs object", client));
                return retrieveRestObject(category, uuid);
        }
    }

    private Object retrieveFhirObject(String category, String uuid) {
        switch (category) {
            case CATEGORY_PATIENT:
                return PatientStrategyUtil.getPatientStrategy().getPatient(uuid);
            case CATEGORY_VISIT:
                return VisitStrategyUtil.getVisitStrategy().getVisit(uuid);
            case CATEGORY_ENCOUNTER:
                return EncounterStrategyUtil.getEncounterStrategy().getEncounter(uuid);
            case CATEGORY_OB:
                return ObservationStrategyUtil.getObservationStrategy().getObservation(uuid);
            case CATEGORY_LOCATION:
                return LocationStrategyUtil.getLocationStrategy().getLocation(uuid);
            default:
                LOGGER.warn(String.format("Unrecognized category %s", category));
                return null;
        }
    }

    private Object retrieveRestObject(String category, String uuid) {
        switch (category) {
            case CATEGORY_PATIENT:
                return Context.getPatientService().getPatientByUuid(uuid);
            case CATEGORY_VISIT:
                return Context.getVisitService().getVisitByUuid(uuid);
            case CATEGORY_ENCOUNTER:
                return Context.getEncounterService().getEncounterByUuid(uuid);
            case CATEGORY_OB:
                return Context.getObsService().getObsByUuid(uuid);
            case CATEGORY_LOCATION:
                return Context.getLocationService().getLocationByUuid(uuid);
            case CATEGORY_PRIVILEGE:
                return Context.getUserService().getPrivilegeByUuid(uuid);
            default:
                LOGGER.warn(String.format("Unrecognized category %s", category));
                return null;
        }
    }

    private void persistRetrievedRestData(Object object, String action) {
        if (object instanceof Patient) {
            persistOpenMrsPatient((Patient) object, action);
        } else if(object instanceof Visit) {
            persistOpenMrsVisit((Visit) object, action);
        } else if(object instanceof Encounter) {
            persistOpenMrsEncounter((Encounter) object, action);
        } else if(object instanceof Obs) {
            persistOpenMrsObservation((Obs) object, action);
        } else if (object instanceof Location) {
            persistOpenMrsLocation((Location) object, action);
        } else if (object instanceof Privilege) {
            persistOpenMrsPrivilege((Privilege) object, action);
        } else {
            LOGGER.warn(String.format("Unrecognized object type %s", object.getClass().getSimpleName()));
        }
    }

    private void persistRetrievedFhirData(Object object, String action) {
        if (object instanceof org.hl7.fhir.dstu3.model.Patient) {
            persistFhirPatient((org.hl7.fhir.dstu3.model.Patient) object, action);
        } else if(object instanceof org.hl7.fhir.dstu3.model.Encounter) {
            persistFhirVisit((org.hl7.fhir.dstu3.model.Encounter) object, action);
        } else if(object instanceof org.hl7.fhir.dstu3.model.Observation) {
            persistFhirOb((org.hl7.fhir.dstu3.model.Observation) object, action);
        } else if (object instanceof org.hl7.fhir.dstu3.model.Location) {
            persistFhirLocation((org.hl7.fhir.dstu3.model.Location) object, action);
        } else {
            LOGGER.warn(String.format("Unrecognized object type %s", object.getClass().getSimpleName()));
        }
    }

    private void persistOpenMrsPatient(Patient patient, String action) {
        PatientService service = Context.getPatientService();
        switch (action) {
            case ACTION_VOIDED:
                Patient retrievedPatient = service.getPatientByUuid(patient.getUuid());
                service.voidPatient(retrievedPatient, VOIDING_REASON);
                break;
            case ACTION_UPDATED:
                Patient updatedPatient = service.getPatientByUuid(patient.getUuid());
                updatePatientAttributes(patient, updatedPatient);
                service.savePatient(updatedPatient);
                break;
            default:
                service.savePatient(patient);
                break;
        }
    }

    private void persistOpenMrsVisit(Visit visit, String action) {
        VisitService service = Context.getVisitService();
        switch (action) {
            case ACTION_VOIDED:
                Visit retrievedVisit = service.getVisitByUuid(visit.getUuid());
                service.voidVisit(retrievedVisit, VOIDING_REASON);
                break;
            case ACTION_UPDATED:
                LOGGER.info("visit action updated");
                Visit updatedVisit = service.getVisitByUuid(visit.getUuid());
                updateVisitAttributes(visit, updatedVisit);
                service.saveVisit(updatedVisit);
                break;
            default:
                service.saveVisit(visit);
                break;
        }
    }

    private void persistOpenMrsEncounter(Encounter encounter, String action) {
        EncounterService service = Context.getEncounterService();
        switch (action) {
            case ACTION_VOIDED:
                Encounter retrievedEncounter = service.getEncounterByUuid(encounter.getUuid());
                service.voidEncounter(retrievedEncounter, VOIDING_REASON);
                break;
            case ACTION_UPDATED:
                Encounter updatedEncounter = service.getEncounterByUuid(encounter.getUuid());
                updateEncounterAttributes(encounter, updatedEncounter);
                service.saveEncounter(updatedEncounter);
                break;
            default:
                service.saveEncounter(encounter);
                break;
        }
    }

    private void persistOpenMrsObservation(Obs obs, String action) {
        ObsService service = Context.getObsService();
        switch (action) {
            case ACTION_VOIDED:
                Obs retrievedOb = service.getObsByUuid(obs.getUuid());
                service.voidObs(retrievedOb, VOIDING_REASON);
                break;
            case ACTION_UPDATED:
                Obs updatedOb = service.getObsByUuid(obs.getUuid());
                updateObAttributes(obs, updatedOb);
                service.saveObs(updatedOb,"");
                break;
            default:
                service.saveObs(obs,"");
                break;
        }
    }

    public static org.openmrs.Patient updatePatientAttributes(Patient newPatient, Patient oldPatient) {
        Set<PersonName> allNames = oldPatient.getNames();

        boolean needToSetPreferredName = false;
        for (PersonName name : newPatient.getNames()) {
            if (name.getPreferred()) {
                needToSetPreferredName = true;
            }
        }
        if (needToSetPreferredName) {
            for (PersonName name : allNames) {
                name.setPreferred(false);
            }
        }
        allNames.addAll(newPatient.getNames());
        oldPatient.setNames(allNames);

        Set<PersonAddress> allAddress = oldPatient.getAddresses();
        for (PersonAddress newAddress : newPatient.getAddresses()) {
            boolean needToAddNew = true;
            for (PersonAddress oldAddress : oldPatient.getAddresses()) {
                if (newAddress.equalsContent(oldAddress)) {
                    needToAddNew = false;
                    break;
                }
            }
            if (needToAddNew) {
                if (newAddress.isPreferred()) {
                    for (PersonAddress address : allAddress) {
                        address.setPreferred(false);
                    }
                }
                allAddress.add(newAddress);
            }
        }
        oldPatient.setAddresses(allAddress);

        oldPatient.setPersonVoided(newPatient.getVoided());
        if (newPatient.getVoided()) {
            oldPatient.setPersonVoidReason(VOIDING_REASON);
        }
        oldPatient.setBirthdate(newPatient.getBirthdate());
        oldPatient.setGender(newPatient.getGender());
        return oldPatient;
    }

    public static org.openmrs.Visit updateVisitAttributes(Visit newVisit, Visit oldVisit) {
        LOGGER.info("update visit attributes");
        oldVisit.setEncounters(newVisit.getEncounters());
        oldVisit.setIndication(newVisit.getIndication());
        oldVisit.setLocation(newVisit.getLocation());
        oldVisit.setPatient(newVisit.getPatient());
        oldVisit.setStartDatetime(newVisit.getStartDatetime());
        oldVisit.setStopDatetime(newVisit.getStopDatetime());
        oldVisit.setVisitType(newVisit.getVisitType());
        oldVisit.setVisitId(newVisit.getVisitId());
        oldVisit.setVoided(newVisit.getVoided());
        oldVisit.setVoidedBy(newVisit.getVoidedBy());
        oldVisit.setVoidReason(newVisit.getVoidReason());
        oldVisit.setDateVoided(newVisit.getDateVoided());
        return new org.openmrs.Visit();
    }

    public static org.openmrs.Encounter updateEncounterAttributes(Encounter newEncounter, Encounter oldEncounter) {
        //TODO
        return new org.openmrs.Encounter();
    }

    public static org.openmrs.Obs updateObAttributes(Obs newObservation, Obs oldObservation) {
        //TODO
        return new org.openmrs.Obs();
    }

    private void persistOpenMrsPrivilege(Privilege privilege, String action) {
        switch (action) {
            case ACTION_VOIDED:
                UserService service = Context.getUserService();
                Privilege purgePrivilege = service.getPrivilegeByUuid(privilege.getUuid());
                service.purgePrivilege(purgePrivilege);
                break;
            case ACTION_UPDATED:
            default:
                Context.getUserService().savePrivilege(privilege);
                break;
        }
    }

    private void persistOpenMrsLocation(Location location, String action) {
        switch (action) {
            case ACTION_VOIDED:
                LocationService service = Context.getLocationService();
                Location retrievedLocation = service.getLocationByUuid(location.getUuid());
                service.retireLocation(retrievedLocation, VOIDING_REASON);
                break;
            case ACTION_UPDATED:
            default:
                Context.getLocationService().saveLocation(location);
                break;
        }
    }

    private void persistFhirPatient(org.hl7.fhir.dstu3.model.Patient patient, String action) {
        switch (action) {
            case ACTION_UPDATED:
                PatientStrategyUtil.getPatientStrategy().updatePatient(patient, patient.getId());
                break;
            case ACTION_VOIDED:
                PatientStrategyUtil.getPatientStrategy().deletePatient(patient.getId());
                break;
            case ACTION_CREATED:
                PatientStrategyUtil.getPatientStrategy().createFHIRPatient(patient);
            default:
                LOGGER.warn(String.format("Unrecognized action: %s", action));
                break;
        }
    }

    private void persistFhirVisit(org.hl7.fhir.dstu3.model.Encounter visit, String action) {
        switch (action) {
            case ACTION_UPDATED:
                VisitStrategyUtil.getVisitStrategy().updateVisit(visit, visit.getId());
                break;
            case ACTION_VOIDED:
                VisitStrategyUtil.getVisitStrategy().deleteVisit(visit.getId());
                break;
            case ACTION_CREATED:
                VisitStrategyUtil.getVisitStrategy().createFHIRVisit(visit);
            default:
                LOGGER.warn(String.format("Unrecognized action: %s", action));
                break;
        }
    }

    private void persistFhirEncounter(org.hl7.fhir.dstu3.model.Encounter encounter, String action) {
        switch (action) {
            case ACTION_UPDATED:
                EncounterStrategyUtil.getEncounterStrategy().updateEncounter(encounter, encounter.getId());
                break;
            case ACTION_VOIDED:
                EncounterStrategyUtil.getEncounterStrategy().deleteEncounter(encounter.getId());
                break;
            case ACTION_CREATED:
                EncounterStrategyUtil.getEncounterStrategy().createFHIREncounter(encounter);
            default:
                LOGGER.warn(String.format("Unrecognized action: %s", action));
                break;
        }
    }

    private void persistFhirOb(org.hl7.fhir.dstu3.model.Observation ob, String action) {
        switch (action) {
            case ACTION_UPDATED:
                ObservationStrategyUtil.getObservationStrategy().updateFHITObservation(ob, ob.getId());
                break;
            case ACTION_VOIDED:
                ObservationStrategyUtil.getObservationStrategy().deleteObservation(ob.getId());
                break;
            case ACTION_CREATED:
                ObservationStrategyUtil.getObservationStrategy().createFHIRObservation(ob);
            default:
                LOGGER.warn(String.format("Unrecognized action: %s", action));
                break;
        }
    }

    private void persistFhirLocation(org.hl7.fhir.dstu3.model.Location location, String action) {
        switch (action) {
            case ACTION_UPDATED:
                LocationStrategyUtil.getLocationStrategy().updateLocation(location.getId(), location);
                break;
            case ACTION_VOIDED:
                LocationStrategyUtil.getLocationStrategy().deleteLocation(location.getId());
                break;
            case ACTION_CREATED:
                LocationStrategyUtil.getLocationStrategy().createLocation(location);
                break;
            default:
                LOGGER.warn(String.format("Unrecognized action: %s", action));
                break;
        }
    }
}
