package org.openmrs.module.sync2.api.sync;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.client.ClientFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.openmrs.module.sync2.SyncConstants.PARENT_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_USERNAME_PROPERTY;

public class SyncClient {

    public Object pullDataFromParent(String category, String clientName, String resourceUrl) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);

        ClientFactory clientFactory = new ClientFactory();
        Client client = clientFactory.createClient(clientName);

        return client.retrieveObject(category, resourceUrl, username, password);
    }

    public ResponseEntity<String> pushDataToParent(Object object, String clientName, String resourceUrl) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);
        Client client = new ClientFactory().createClient(clientName);

        try {
            return client.createObject(resourceUrl, username, password, object);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new SyncException(String.format("Object posting error. Code: %d. Details: \n%s",
                    e.getStatusCode().value(), e.getResponseBodyAsString()), e);
        }
    }
}
