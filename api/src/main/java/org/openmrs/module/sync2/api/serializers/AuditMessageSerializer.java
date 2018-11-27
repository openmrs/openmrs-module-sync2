package org.openmrs.module.sync2.api.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AuditMessageSerializer implements JsonSerializer<AuditMessage> {

	@Override
	public JsonElement serialize(AuditMessage src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		DateFormat formatter = new SimpleDateFormat(SyncConstants.AUDIT_MESSAGE_DATE_FORMAT);

		object.addProperty("id", src.getId());
		object.addProperty("uuid", src.getUuid());
		object.addProperty("success", src.getSuccess());
		object.addProperty("timestamp", formatter.format(src.getTimestamp()));
		object.addProperty("resourceName", src.getResourceName());
		object.addProperty("usedResourceUrl", src.getUsedResourceUrl());
		object.addProperty("availableResourceUrls", src.getAvailableResourceUrls());
		object.addProperty("parentUrl", src.getParentUrl());
		object.addProperty("localUrl", src.getLocalUrl());
		object.addProperty("action", src.getAction());
		object.addProperty("operation", src.getOperation());
		object.addProperty("details", src.getDetails());
		object.addProperty("linkType", src.getLinkType());
		object.addProperty("nextMessageUuid", src.getNextMessageUuid());
		object.addProperty("creatorInstanceId", src.getCreatorInstanceId());
		object.addProperty("voided", src.getVoided());
		object.addProperty("mergeConflictUuid", src.getMergeConflictUuid());
		return object;
	}
}
