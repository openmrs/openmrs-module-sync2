package org.openmrs.module.sync2.api.model.configuration;

import java.util.Objects;

public class SyncConfiguration {

    private GeneralConfiguration general;
    private SyncMethodConfiguration push;
    private SyncMethodConfiguration pull;
    private WhitelistConfiguration whitelist;

    public SyncConfiguration() {
        whitelist = new WhitelistConfiguration();
    }

    public SyncConfiguration(GeneralConfiguration general, SyncMethodConfiguration push, SyncMethodConfiguration pull,
                             WhitelistConfiguration whitelist) {
        this.general = general;
        this.push = push;
        this.pull = pull;
        this.whitelist = whitelist;
    }

    public GeneralConfiguration getGeneral() {
        return general;
    }

    public void setGeneral(GeneralConfiguration general) {
        this.general = general;
    }

    public SyncMethodConfiguration getPush() {
        return push;
    }

    public void setPush(SyncMethodConfiguration push) {
        this.push = push;
    }

    public SyncMethodConfiguration getPull() {
        return pull;
    }

    public void setPull(SyncMethodConfiguration pull) {
        this.pull = pull;
    }

    public WhitelistConfiguration getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(WhitelistConfiguration whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SyncConfiguration that = (SyncConfiguration) o;
        return Objects.equals(general, that.general)
                && Objects.equals(push, that.push)
                && Objects.equals(pull, that.pull)
                && Objects.equals(whitelist, that.whitelist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(general, push, pull, whitelist);
    }
}
