package org.openmrs.module.sync2.api.sync;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.openmrs.Location;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.Privilege;
import org.openmrs.api.PatientService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.strategies.location.LocationStrategyUtil;
import org.openmrs.module.fhir.api.strategies.patient.PatientStrategyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT_KEY;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT_KEY;

public class SyncPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPersistence.class);

    private static final String CATEGORY_PATIENT = "patient";
    private static final String CATEGORY_LOCATION = "location";
    private static final String CATEGORY_PRIVILEGE = "privilege";
    
    private static final String ACTION_DELETED = "DELETED";
    private static final String ACTION_UPDATED = "UPDATED";
    private static final String ACTION_CREATED = "CREATED";
    
    private static final String VOIDING_REASON = "Voided by Sync 2";
    
    public void persistRetrievedData(Object retrievedObject, String action) {
        if (retrievedObject instanceof OpenmrsObject) {
            persistRetrievedRestData(retrievedObject, action);
        } else if (retrievedObject instanceof DomainResource) {
            persistRetrievedFhirData(retrievedObject, action);
        } else {
            LOGGER.warn(String.format("Unrecognized object type %s", retrievedObject.getClass().getSimpleName()));
        }
    }

    public Object retrieveData(String client, String category, String uuid) {
        switch (client) {
            case FHIR_CLIENT_KEY:
                return retrieveFhirObject(category, uuid);
            case REST_CLIENT_KEY:
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
        } else if (object instanceof org.hl7.fhir.dstu3.model.Location) {
            persistFhirLocation((org.hl7.fhir.dstu3.model.Location) object, action);
        } else {
            LOGGER.warn(String.format("Unrecognized object type %s", object.getClass().getSimpleName()));
        }
    }

    private void persistOpenMrsPatient(Patient patient, String action) {
        switch (action) {
            case ACTION_DELETED:
                PatientService service = Context.getPatientService();
                Patient retrievedPatient = service.getPatientByUuid(patient.getUuid());
                service.voidPatient(retrievedPatient, VOIDING_REASON);
                break;
            case ACTION_UPDATED:
            default:
                Context.getPatientService().savePatient(patient);
                break;
        }
    }

    private void persistOpenMrsPrivilege(Privilege privilege, String action) {
        switch (action) {
            case ACTION_DELETED:
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
            case ACTION_DELETED:
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
            case ACTION_DELETED:
                PatientStrategyUtil.getPatientStrategy().deletePatient(patient.getId());
                break;
            case ACTION_CREATED:
                PatientStrategyUtil.getPatientStrategy().createFHIRPatient(patient);
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
            case ACTION_DELETED:
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
