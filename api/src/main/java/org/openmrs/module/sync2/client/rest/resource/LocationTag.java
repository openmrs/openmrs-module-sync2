package org.openmrs.module.sync2.client.rest.resource;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.context.Context;

import java.util.List;
import java.util.Objects;

public class LocationTag implements RestResource {
    private String uuid;
    private String name;
    private String description;
    private Boolean retired;
    private List<Link> links;
    private String resourceVersion;

    // region getters
    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getRetired() {
        return retired;
    }

    public List<Link> getLinks() {
        return links;
    }

    public String getResourceVersion() {
        return resourceVersion;
    }
    // endregion

    // region setters
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }
    // endregion

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        if (uuid != null) {
            org.openmrs.LocationTag locationTag = Context.getLocationService().getLocationTagByUuid(uuid);
            if (locationTag != null) {
                return locationTag;
            }
        }

        return Context.getLocationService().getLocationTagByName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocationTag that = (LocationTag) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(retired, that.retired) &&
                Objects.equals(links, that.links) &&
                Objects.equals(resourceVersion, that.resourceVersion);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, description, retired, links, resourceVersion);
    }
}
