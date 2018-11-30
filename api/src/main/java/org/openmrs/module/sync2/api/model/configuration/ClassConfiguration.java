package org.openmrs.module.sync2.api.model.configuration;

import java.io.Serializable;
import java.util.Objects;

public class ClassConfiguration implements Serializable {

    private static final long serialVersionUID = -6874334396673035705L;

    private String classTitle;

    private String category;

    private String openMrsClass;

    private boolean enabled;

    private String preferredClient;

    public ClassConfiguration() { }

    public ClassConfiguration(String classTitle, String category, String openMrsClass, boolean enabled) {
        this.classTitle = classTitle;
        this.category = category;
        this.openMrsClass = openMrsClass;
        this.enabled = enabled;
    }

    public ClassConfiguration(String classTitle, String category, String openMrsClass, boolean enabled,
            String preferredClient) {
        this.classTitle = classTitle;
        this.category = category;
        this.openMrsClass = openMrsClass;
        this.enabled = enabled;
        this.preferredClient = preferredClient;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOpenMrsClass() {
        return openMrsClass;
    }

    public void setOpenMrsClass(String openMrsClass) {
        this.openMrsClass = openMrsClass;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPreferredClient() {
        return preferredClient;
    }

    public void setPreferredClient(String preferredClient) {
        this.preferredClient = preferredClient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassConfiguration that = (ClassConfiguration) o;
        return enabled == that.enabled
                && Objects.equals(classTitle, that.classTitle)
                && Objects.equals(category, that.category)
                && Objects.equals(openMrsClass, that.openMrsClass)
                && Objects.equals(preferredClient, that.preferredClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classTitle, category, openMrsClass, enabled, preferredClient);
    }
}
