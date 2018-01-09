package org.openmrs.module.sync2.api.sync;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.client.ClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.PARENT_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_USERNAME_PROPERTY;

public class SyncClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncClient.class);

    public Object pullData(String category, String clientName, String resourceUrl) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);

        ClientFactory clientFactory = new ClientFactory();
        Client client = clientFactory.createClient(clientName);

        return client.retrieveObject(category, resourceUrl, username, password);
    }

    public ResponseEntity<String> pushData(Object object, String clientName, String resourceUrl, String action) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);
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
}
