package org.openmrs.module.sync2.api.sync;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.ClientFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

import static org.openmrs.module.sync2.SyncConstants.RESOURCE_PREFERRED_CLIENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getPreferredUrl;

public class SyncClient {

    private static final String PARENT_USERNAME_PROPERTY = "sync2.user.login";
    private static final String PARENT_PASSWORD_PROPERTY = "sync2.user.password";

    public Object pullDataFromParent(String category, String clientName, String pushUrl) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);

        ClientFactory clientFactory = new ClientFactory();
        Client client = clientFactory.createClient(clientName);
        return client.retrieveObject(category, pushUrl, username, password);
    }

    public ResponseEntity<String> pushDataToParent(Object object, String clientName, String pushUrl) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);

        Client client = new ClientFactory().createClient(clientName);
        try {
            return client.createObject(pushUrl, username, password, object);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new SyncException(String.format("Object posting error. Code: %d. Details: \n%s",
                    e.getStatusCode().value(), e.getResponseBodyAsString()), e);
        }
    }
}
