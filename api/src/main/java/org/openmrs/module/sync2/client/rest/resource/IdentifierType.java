package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;

import java.util.List;

public class IdentifierType implements RestResource {
    private String uuid;
    private String display;
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
    // endregion

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        PatientIdentifierType patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(uuid);
        return patientIdentifierType;
    }

}
