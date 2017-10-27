package org.openmrs.module.sync2.api.model.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sync2MethodConfiguration {

    private boolean enabled;
    private Integer schedule;
    private List<ClassConfiguration> classes;

    public Sync2MethodConfiguration() {
        classes = new ArrayList<ClassConfiguration>();
    }

    public Sync2MethodConfiguration(boolean enabled, Integer schedule, List<ClassConfiguration> classes) {
        this.enabled = enabled;
        this.schedule = schedule;
        this.classes = classes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    public List<ClassConfiguration> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassConfiguration> classes) {
        this.classes = classes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sync2MethodConfiguration that = (Sync2MethodConfiguration) o;
        return enabled == that.enabled
                && Objects.equals(schedule, that.schedule)
                && Objects.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, schedule, classes);
    }
}
