package org.openmrs.module.sync2.api.sync;

public class FhirPulledObject extends PulledObject {

    public FhirPulledObject(Object resourceObject) {
        super(resourceObject);
    }

    @Override
    public Class getClientType() {
        return FhirPulledObject.class;
    }
}
