package org.openmrs.module.sync2.api.model.configuration;

import java.util.List;
import java.util.Objects;

public class WhitelistConfiguration {

    private boolean enabled;
    private List<String> instanceIds;

    public WhitelistConfiguration() {}

    public WhitelistConfiguration(boolean enabled, List<String> instanceIds) {
        this.enabled = enabled;
        this.instanceIds = instanceIds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WhitelistConfiguration that = (WhitelistConfiguration) o;
        return enabled == that.enabled
                && Objects.equals(instanceIds, that.instanceIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, instanceIds);
    }
}
