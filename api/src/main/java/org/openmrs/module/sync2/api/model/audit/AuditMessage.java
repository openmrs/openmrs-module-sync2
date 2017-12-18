package org.openmrs.module.sync2.api.model.audit;


import java.lang.reflect.Type;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.hibernate.annotations.Persister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.openmrs.BaseOpenmrsData;

@Persister(impl = SingleTableEntityPersister.class)
public class AuditMessage extends BaseOpenmrsData {
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

    private Integer nextMessage;

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

    @Override
    public String getUuid() {
        return super.getUuid();
    }

    @Override
    public void setUuid(String uuid) {
        super.setUuid(uuid);
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

    public Integer getNextMessage() {
        return nextMessage;
    }

    public void setNextMessage(Integer nextMessage) {
        this.nextMessage = nextMessage;
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
                && Objects.equals(this.nextMessage, auditMessage.nextMessage);
    }



    @Override
    public int hashCode() {

        return Objects.hash(success, timestamp, resourceName, usedResourceUrl, availableResourceUrls, parentUrl,
                localUrl, action, details, action, linkType, nextMessage);
    }
    
    public static class AuditMessageSerializer implements JsonSerializer<AuditMessage> {

        @Override
        public JsonElement serialize(AuditMessage src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            object.addProperty("id", src.id);
            object.addProperty("uuid", src.getUuid());
            object.addProperty("success", src.success);
            object.addProperty("timestamp", formatter.format(src.timestamp));
            object.addProperty("resourceName", src.resourceName);
            object.addProperty("usedResourceUrl", src.usedResourceUrl);
            object.addProperty("availableResourceUrls", src.availableResourceUrls);
            object.addProperty("parentUrl", src.parentUrl);
            object.addProperty("localUrl", src.localUrl);
            object.addProperty("action", src.action);
            object.addProperty("operation", src.operation);
            object.addProperty("details", src.details);
            object.addProperty("linkType", src.linkType);
            object.addProperty("nextMessage", src.nextMessage);

            return object;
        }
    }

}