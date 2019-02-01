package org.openmrs.module.sync2.api.model.audit;

import org.hibernate.annotations.Persister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.rest.resource.RestResource;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Persister(impl = SingleTableEntityPersister.class)
public class AuditMessage extends BaseOpenmrsData implements RestResource, Serializable {

    private static final long serialVersionUID = 6106269076155338045L;

    private Integer id;

    private Boolean success;

    private Date timestamp;

    private String resourceName;

    private String usedResourceUrl;

    private String availableResourceUrls;

    private String parentUrl;

    private String localUrl;

    private String details;

    private String action;

    private String operation;

    private String linkType;

    private String nextMessageUuid;
    
    private String creatorInstanceId;

    private String mergeConflictUuid;

    public AuditMessage() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = new Date(timestamp.getTime());
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getUsedResourceUrl() {
        return usedResourceUrl;
    }

    public void setUsedResourceUrl(String usedResourceUrl) {
        this.usedResourceUrl = usedResourceUrl;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public String getAvailableResourceUrls() {
        return availableResourceUrls;
    }
    
    public Map<String, String> getAvailableResourceUrlsAsMap() {
        return SyncUtils.deserializeJsonToStringsMap(availableResourceUrls);
    }
    
    public void setAvailableResourceUrls(String availableResourceUrls) {
        this.availableResourceUrls = availableResourceUrls;
    }
    
    public String getParentUrl() {
        return parentUrl;
    }
    
    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }
    
    public String getLocalUrl() {
        return localUrl;
    }
    
    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getNextMessageUuid() {
        return nextMessageUuid;
    }
    
    public void setNextMessageUuid(String nextMessageUuid) {
        this.nextMessageUuid = nextMessageUuid;
    }
    
    public String getCreatorInstanceId() {
        return creatorInstanceId;
    }
    
    public void setCreatorInstanceId(String creatorInstanceId) {
        this.creatorInstanceId = creatorInstanceId;
    }

    public String getMergeConflictUuid() {
        return mergeConflictUuid;
    }

    public void setMergeConflictUuid(String mergeConflictUuid) {
        this.mergeConflictUuid = mergeConflictUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuditMessage auditMessage = (AuditMessage) o;
        return Objects.equals(this.getUuid(), auditMessage.getUuid())
                && Objects.equals(this.success, auditMessage.success)
                && Objects.equals(this.timestamp, auditMessage.timestamp)
                && Objects.equals(this.resourceName, auditMessage.resourceName)
                && Objects.equals(this.usedResourceUrl, auditMessage.usedResourceUrl)
                && Objects.equals(this.availableResourceUrls, auditMessage.availableResourceUrls)
                && Objects.equals(this.parentUrl, auditMessage.parentUrl)
                && Objects.equals(this.localUrl, auditMessage.localUrl)
                && Objects.equals(this.action, auditMessage.action)
                && Objects.equals(this.operation, auditMessage.operation)
                && Objects.equals(this.details, auditMessage.details)
                && Objects.equals(this.action, auditMessage.action)
                && Objects.equals(this.operation, auditMessage.operation)
                && Objects.equals(this.linkType, auditMessage.linkType)
                && Objects.equals(this.nextMessageUuid, auditMessage.nextMessageUuid)
                && Objects.equals(this.creatorInstanceId, auditMessage.creatorInstanceId)
                && Objects.equals(this.getVoided(), auditMessage.getVoided())
                && Objects.equals(this.mergeConflictUuid, auditMessage.getMergeConflictUuid());
    }
    
    @Override
    public int hashCode() {

        return Objects.hash(success, timestamp, resourceName, usedResourceUrl, availableResourceUrls, parentUrl,
                localUrl, action, details, action, linkType, nextMessageUuid, creatorInstanceId, mergeConflictUuid);
    }
    
    @Override
    public String toString() {
        return "AuditMessage{" +
                "uuid='" + getUuid() + '\'' +
                ", id=" + id +
                ", success=" + success +
                ", timestamp=" + timestamp +
                ", resourceName='" + resourceName + '\'' +
                ", usedResourceUrl='" + usedResourceUrl + '\'' +
                ", availableResourceUrls='" + availableResourceUrls + '\'' +
                ", parentUrl='" + parentUrl + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", details='" + details + '\'' +
                ", action='" + action + '\'' +
                ", operation='" + operation + '\'' +
                ", linkType='" + linkType + '\'' +
                ", nextMessageUuid='" + nextMessageUuid + '\'' +
                ", creatorInstanceId='" + creatorInstanceId + '\'' +
                ", voided='" + getVoided() + '\'' +
                ", mergeConflictUuid = '" + mergeConflictUuid + '\'' +
                '}';
    }

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        return this;
    }

}
