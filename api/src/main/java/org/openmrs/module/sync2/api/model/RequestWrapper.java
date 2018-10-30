package org.openmrs.module.sync2.api.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.openmrs.module.sync2.api.utils.SyncUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Objects;

public class RequestWrapper implements Serializable {

	protected String instanceId;

	protected String clientName;

	protected String className;

	protected InnerRequest request;

	public RequestWrapper() {
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClassName() {
		return className;
	}

	public Class<?> getClazz() throws ClassNotFoundException {
		return Class.forName(className);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public InnerRequest getRequest() {
		return request;
	}

	public void setRequest(InnerRequest request) {
		this.request = request;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof RequestWrapper))
			return false;
		RequestWrapper that = (RequestWrapper) o;
		return Objects.equals(instanceId, that.instanceId) &&
				Objects.equals(clientName, that.clientName) &&
				Objects.equals(className, that.className) &&
				Objects.equals(request, that.request);
	}

	@Override
	public int hashCode() {
		return Objects.hash(instanceId, clientName, className, request);
	}

	public static class RequestWrapperSerializer implements JsonSerializer<RequestWrapper> {

		@Override
		public JsonElement serialize(RequestWrapper src, Type typeOfSrc, JsonSerializationContext context) {
			return SyncUtils.createDefaultGson().toJsonTree(src);
		}
	}

	public static class RequestWrapperDeserializer implements JsonDeserializer<RequestWrapper> {

		@Override
		public RequestWrapper deserialize(JsonElement json, Type type,
				JsonDeserializationContext jsonDeserializationContext) {
			return SyncUtils.createDefaultGson().fromJson(json, RequestWrapper.class);
		}
	}
}
