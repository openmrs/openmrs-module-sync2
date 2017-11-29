package org.openmrs.module.sync2.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.fhir.api.client.fhir.FHIRClientFactory;
import org.openmrs.module.sync2.client.rest.RestClientFactory;

public class ClientFactory {
    protected final Log log = LogFactory.getLog(this.getClass());

    private static final String REST_CLIENT_KEY = "rest";
    private static final String FHIR_CLIENT_KEY = "fhir";


    public Client createClient(final String clientType) {
        switch (clientType) {
            case REST_CLIENT_KEY:
                return RestClientFactory.createClient();
            case FHIR_CLIENT_KEY:
                return FHIRClientFactory.createClient();
            default:
                log.warn(String.format("Unrecognized clientType: %s", clientType));
                return null;
        }
    }
}
