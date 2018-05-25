package org.openmrs.module.sync2.client.rest.resource;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Observation implements RestResource {

    private String uuid;
    private String display;

    @Expose
    private List<Identifier> identifiers = new ArrayList<Identifier>();

    public Observation() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }


    /**
     * Converts resource Observation to org.openmrs.Observation
     * @return
     */
    @Override
    public org.openmrs.BaseOpenmrsObject getOpenMrsObject() {
        //TODO
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Observation ob = (Observation) o;
        return Objects.equals(uuid, ob.uuid) &&
                Objects.equals(display, ob.display) &&
                Objects.equals(identifiers, ob.identifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, display, identifiers);
    }
}
