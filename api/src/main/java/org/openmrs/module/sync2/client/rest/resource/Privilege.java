package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;
import org.openmrs.BaseOpenmrsObject;

public class Privilege implements RestResource {

    private String uuid;
    private Boolean retired;

    @Expose
    private String name;
    @Expose
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getRetired() {
        return retired;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        org.openmrs.Privilege object = new org.openmrs.Privilege();
        object.setUuid(this.uuid);
        object.setPrivilege(this.name);
        object.setDescription(this.description);
        object.setRetired(this.retired);
        return object;
    }
}
