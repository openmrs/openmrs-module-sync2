package org.openmrs.module.sync2.client.rest.resource;

import java.util.Objects;

public class Link {
    private String rel;
    private String url;

    public String getRel() {
        return rel;
    }

    public String getUrl() {
        return url;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Link link = (Link) o;
        return Objects.equals(rel, link.rel) &&
                Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, url);
    }
}
