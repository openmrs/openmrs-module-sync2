package org.openmrs.module.sync2.api.sync;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.client.ClientFactory;

import java.util.Map;

public class SyncClient {

    private static final String PARENT_USERNAME_PROPERTY = "sync2.user.login";
    private static final String PARENT_PASSWORD_PROPERTY = "sync2.user.password";
    private static final String RESOURCE_PREFERRED_CLIENT = "sync2.resource.preferred.client";

    public Object pullDataFromParent(String category, Map<String, String> resourceLinks, String address) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);
        String preferredClient = Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);
        String url = address + resourceLinks.get(preferredClient);

        ClientFactory clientFactory = new ClientFactory();

        Client client = clientFactory.createClient(preferredClient);

        return client.getObject(category, url, username, password);
    }

    public Object pushDataToParent(Object object, Map<String, String> resourceLinks, String address) {
        String username = Context.getAdministrationService().getGlobalProperty(PARENT_USERNAME_PROPERTY);
        String password = Context.getAdministrationService().getGlobalProperty(PARENT_PASSWORD_PROPERTY);
        String preferredClient = Context.getAdministrationService().getGlobalProperty(RESOURCE_PREFERRED_CLIENT);
        String url = address + resourceLinks.get(preferredClient);

        ClientFactory clientFactory = new ClientFactory();

        return clientFactory.createClient(preferredClient).postObject(url, username, password, object);
    }
}
