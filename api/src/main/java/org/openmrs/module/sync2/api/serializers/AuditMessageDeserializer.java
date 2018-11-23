package org.openmrs.module.sync2.api.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;

public class AuditMessageDeserializer implements JsonDeserializer<AuditMessage> {

	@Autowired
	private MergeConflictService mergeConflictService;

	@Override
	public AuditMessage deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) {
		Gson gson = new GsonBuilder().setDateFormat(SyncConstants.AUDIT_MESSAGE_DATE_FORMAT).create();
		return gson.fromJson(jsonElement, type);
	}
}
