package org.openmrs.module.sync2.api.model.configuration;

import java.util.Objects;

public class Sync2Configuration {

    private GeneralConfiguration general;
    private Sync2MethodConfiguration push;
    private Sync2MethodConfiguration pull;

    public Sync2Configuration() { }

    public Sync2Configuration(GeneralConfiguration general, Sync2MethodConfiguration push, Sync2MethodConfiguration pull) {
        this.general = general;
        this.push = push;
        this.pull = pull;
    }

    public GeneralConfiguration getGeneral() {
        return general;
    }

    public void setGeneral(GeneralConfiguration general) {
        this.general = general;
    }

    public Sync2MethodConfiguration getPush() {
        return push;
    }

    public void setPush(Sync2MethodConfiguration push) {
        this.push = push;
    }

    public Sync2MethodConfiguration getPull() {
        return pull;
    }

    public void setPull(Sync2MethodConfiguration pull) {
        this.pull = pull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sync2Configuration that = (Sync2Configuration) o;
        return Objects.equals(general, that.general)
                && Objects.equals(push, that.push)
                && Objects.equals(pull, that.pull);
    }

    @Override
    public int hashCode() {
        return Objects.hash(general, push, pull);
    }
}
