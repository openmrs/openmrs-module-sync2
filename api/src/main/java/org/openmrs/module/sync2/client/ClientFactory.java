package org.openmrs.module.sync2.client;

import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.fhir.api.client.FHIRClientFactory;
import org.openmrs.module.sync2.client.rest.RestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactory.class);

    private static final String REST_CLIENT_KEY = "rest";
    private static final String FHIR_CLIENT_KEY = "fhir";


    public Client createClient(final String clientType) {
        switch (clientType) {
            case REST_CLIENT_KEY:
                return RestClientFactory.createClient();
            case FHIR_CLIENT_KEY:
                return FHIRClientFactory.createClient();
            default:
                LOGGER.warn(String.format("Unrecognized clientType: %s", clientType));
                return null;
        }
    }
}
