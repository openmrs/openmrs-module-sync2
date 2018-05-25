package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.BaseOpenmrsObject;

/**
 * Created by tomasz on 24.05.18.
 */
public class VisitType implements RestResource {

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Expose
    private String uuid;

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        org.openmrs.VisitType visitType = new org.openmrs.VisitType();
        visitType.setUuid(uuid);
        return visitType;
    }
}