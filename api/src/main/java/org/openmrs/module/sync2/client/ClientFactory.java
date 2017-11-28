package org.openmrs.module.sync2.client;

import org.openmrs.module.sync2.client.rest.RestClient;
import org.openmrs.module.fhir.api.client.FHIRClient;

public class ClientFactory {

    private static final String REST_CLIENT_KEY = "rest";
    private static final String FHIR_CLIENT_KEY = "fhir";

    public Object createClient(final String clientType) {
        switch (clientType) {
            case REST_CLIENT_KEY:
                return new RestClient();
            case FHIR_CLIENT_KEY:
                return new FHIRClient();
            default:
                return null;
        }
    }
}
