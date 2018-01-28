package org.openmrs.module.sync2.api.sync;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.client.ClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.LOCAL_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.LOCAL_USERNAME_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_USERNAME_PROPERTY;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.CHILD;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;

public class SyncClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncClient.class);

    private String username;
    private String password;

    public Object pullData(String category, String clientName, String resourceUrl, OpenMRSSyncInstance instance) {
        Object result;
        setUpCredentials(instance);

        ClientFactory clientFactory = new ClientFactory();
        Client client = clientFactory.createClient(clientName);

        try {
            result = client.retrieveObject(category, resourceUrl, username, password);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                result = null;
            } else {
                throw new SyncException("Error during reading local object: ", e);
            }
        }

        return result;
    }

    public ResponseEntity<String> pushData(Object object, String clientName,
                                           String resourceUrl, String action, OpenMRSSyncInstance instance) {
        setUpCredentials(instance);

        Client client = new ClientFactory().createClient(clientName);

        try {
            switch (action) {
                case ACTION_CREATED:
                    return client.createObject(resourceUrl, username, password, object);
                case ACTION_UPDATED:
                    return client.updateObject(resourceUrl, username, password, object);
                case ACTION_VOIDED:
                    return client.deleteObject(resourceUrl, username, password, (String) object);
                default:
                    LOGGER.warn(String.format("Sync push exception. Unrecognized action: %s", action));
                    break;
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new SyncException(String.format("Object posting error. Code: %d. Details: \n%s",
                    e.getStatusCode().value(), e.getResponseBodyAsString()), e);
        }
        return null;
    }

    private void setUpCredentials(OpenMRSSyncInstance instance) {
        AdministrationService adminService = Context.getAdministrationService();

        this.username = instance.equals(PARENT) ? adminService.getGlobalProperty(PARENT_USERNAME_PROPERTY) :
                adminService.getGlobalProperty(LOCAL_USERNAME_PROPERTY);

        this.password = instance.equals(PARENT) ? adminService.getGlobalProperty(PARENT_PASSWORD_PROPERTY) :
                adminService.getGlobalProperty(LOCAL_PASSWORD_PROPERTY);
    }
}
