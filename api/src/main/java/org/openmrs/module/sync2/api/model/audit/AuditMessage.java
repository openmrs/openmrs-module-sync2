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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Persister(impl = SingleTableEntityPersister.class)
@Entity
@Table(name = "sync_audit_message")
public class AuditMessage extends BaseOpenmrsData {
    private static final long serialVersionUID = 6106269076155338045L;

    @Id
    @GeneratedValue
    @Column(name = "sync_audit_message_id")
    private Integer id;

    @Basic
    @Column(name = "success")
    private Boolean success;

    @Basic
    @Column(name = "timestamp")
    private Date timestamp;

    @Basic
    @Column(name = "resource_name")
    private String resourceName;

    @Basic
    @Column(name = "resource_url")
    private String resourceUrl;

    @Basic
    @Column(name = "action")
    private String action;

    @Basic
    @Column(name = "error")
    private String error;


    public AuditMessage() {
    }

    public AuditMessage(Integer id, Boolean success, Date timestamp, String resourceName, String resourceUrl) {
        this.id = id;
        this.success = success;
        this.timestamp = new Date(timestamp.getTime());
        this.resourceName = resourceName;
        this.resourceUrl = resourceUrl;
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

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
        return Objects.equals(this.success, auditMessage.success)
                && Objects.equals(this.timestamp, auditMessage.timestamp)
                && Objects.equals(this.resourceName, auditMessage.resourceName)
                && Objects.equals(this.resourceUrl, auditMessage.resourceUrl)
                && Objects.equals(this.error, auditMessage.error)
                && Objects.equals(this.action, auditMessage.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, timestamp, resourceName, resourceUrl, error, action);
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
            object.addProperty("resourceUrl", src.resourceUrl);
            object.addProperty("error", src.error);
            object.addProperty("action", src.action);

            return object;
        }
    }

}