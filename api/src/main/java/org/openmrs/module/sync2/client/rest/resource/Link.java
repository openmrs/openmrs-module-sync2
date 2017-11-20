package org.openmrs.module.sync2.client.rest.resource;

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
}
