package org.openmrs.module.sync2.api.sync;

public abstract class PulledObject {

    private Object resourceObject;

    protected PulledObject(Object resourceObject) {
        this.resourceObject = resourceObject;
    }

    public Object getResourceObject() {
        return resourceObject;
    }

    public abstract Class getClientType();
}
