package org.openmrs.module.sync2.api.model.audit;


import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.hibernate.annotations.Persister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.client.rest.resource.RestResource;

@Persister(impl = SingleTableEntityPersister.class)
public class AuditMessage extends BaseOpenmrsData implements RestResource {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
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
                && Objects.equals(this.getVoided(), auditMessage.getVoided());
    }
    
    @Override
    public int hashCode() {

        return Objects.hash(success, timestamp, resourceName, usedResourceUrl, availableResourceUrls, parentUrl,
                localUrl, action, details, action, linkType, nextMessageUuid, creatorInstanceId);
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
                '}';
    }

    @Override
    public BaseOpenmrsObject getOpenMrsObject() {
        return this;
    }

    public static class AuditMessageSerializer implements JsonSerializer<AuditMessage> {
        
        @Override
        public JsonElement serialize(AuditMessage src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

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
            object.addProperty("nextMessageUuid", src.nextMessageUuid);
            object.addProperty("creatorInstanceId", src.creatorInstanceId);
            object.addProperty("voided", src.getVoided());

            return object;
        }
    }
    
    public static class AuditMessageDeserializer implements JsonDeserializer<AuditMessage> {
        
        @Override
        public AuditMessage deserialize(JsonElement jsonElement, Type type,
                                        JsonDeserializationContext jsonDeserializationContext) {
            Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
            return gson.fromJson(jsonElement, type);
        }
    }
    
}