package org.openmrs.module.sync2.api.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AuditMessageToStringConverter implements Converter<AuditMessage, String> {

    @Override
    public String convert(AuditMessage auditMessage) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AuditMessage.class, new AuditMessage.AuditMessageSerializer())
                .create();
        return gson.toJson(auditMessage);
    }
}
