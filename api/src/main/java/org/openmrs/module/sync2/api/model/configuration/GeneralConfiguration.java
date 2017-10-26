package org.openmrs.module.sync2.api.model.configuration;

import java.util.Objects;

public class GeneralConfiguration {

    private String parentFeedLocation;
    private String localFeedLocation;

    public GeneralConfiguration() { }

    public GeneralConfiguration(String parentFeedLocation, String localFeedLocation) {
        this.parentFeedLocation = parentFeedLocation;
        this.localFeedLocation = localFeedLocation;
    }

    public String getParentFeedLocation() {
        return parentFeedLocation;
    }

    public void setParentFeedLocation(String parentFeedLocation) {
        this.parentFeedLocation = parentFeedLocation;
    }

    public String getLocalFeedLocation() {
        return localFeedLocation;
    }

    public void setLocalFeedLocation(String localFeedLocation) {
        this.localFeedLocation = localFeedLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneralConfiguration that = (GeneralConfiguration) o;
        return Objects.equals(parentFeedLocation, that.parentFeedLocation) &&
                Objects.equals(localFeedLocation, that.localFeedLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentFeedLocation, localFeedLocation);
    }
}
