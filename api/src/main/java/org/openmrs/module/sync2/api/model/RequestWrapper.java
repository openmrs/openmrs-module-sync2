package org.openmrs.module.sync2.api.model;

import org.springframework.http.RequestEntity;
import java.util.Objects;

public class RequestWrapper {

    private String instanceId;
    private RequestEntity requestEntity;

    public RequestWrapper() {
    }

    public RequestWrapper(String instanceId, RequestEntity requestEntity) {
        this.instanceId = instanceId;
        this.requestEntity = requestEntity;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestWrapper)) return false;
        RequestWrapper that = (RequestWrapper) o;
        return Objects.equals(instanceId, that.instanceId) &&
                Objects.equals(requestEntity, that.requestEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, requestEntity);
    }
}
