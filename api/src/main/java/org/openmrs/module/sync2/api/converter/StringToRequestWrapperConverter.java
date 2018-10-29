package org.openmrs.module.sync2.api.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRequestWrapperConverter implements Converter<String, RequestWrapper> {

    @Override
    public RequestWrapper convert(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RequestWrapper.class, new RequestWrapper.RequestWrapperDeserializer())
                .create();
        return gson.fromJson(json, RequestWrapper.class);
    }
}
