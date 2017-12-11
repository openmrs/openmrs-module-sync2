package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.context.Context;

import java.util.List;

public class IdentifierType implements RestResource {
    private String uuid;
    private String display;
    private String name;
    private String description;
    private List<Link> links;

    // region getters
    public String getUuid() {
        return uuid;
    }

    public String getDisplay() {
        return display;
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    // endregion

    // region setters
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // endregion

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        return Context.getPatientService().getPatientIdentifierTypeByUuid(uuid);
    }

}
