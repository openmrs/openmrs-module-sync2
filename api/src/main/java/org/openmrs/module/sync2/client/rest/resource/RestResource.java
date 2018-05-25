package org.openmrs.module.sync2.client.rest.resource;

import org.codehaus.jackson.annotate.JsonIgnore;

public interface RestResource {

    @JsonIgnore
    org.openmrs.BaseOpenmrsObject getOpenMrsObject();
}
