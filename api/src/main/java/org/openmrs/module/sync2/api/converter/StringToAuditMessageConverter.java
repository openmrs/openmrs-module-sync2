package org.openmrs.module.sync2.api.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToAuditMessageConverter implements Converter<String, AuditMessage> {
   
    @Override
    public AuditMessage convert(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AuditMessage.class, AuditMessage.AuditMessageDeserializer.class)
                .create();
        return gson.fromJson(json, AuditMessage.class);
    }
}
