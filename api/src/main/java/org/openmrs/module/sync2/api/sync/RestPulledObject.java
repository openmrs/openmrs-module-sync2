package org.openmrs.module.sync2.api.sync;

import org.openmrs.module.sync2.client.rest.RestClient;

public class RestPulledObject extends PulledObject {

    protected RestPulledObject(Object resourceObject) {
        super(resourceObject);
    }

    @Override
    public Class getClientType() {
        return RestClient.class;
    }
}
