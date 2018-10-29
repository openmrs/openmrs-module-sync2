package org.openmrs.module.sync2.api.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RequestWrapperToStringConverter implements Converter<RequestWrapper, String> {

    @Override
    public String convert(RequestWrapper wrapper) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RequestWrapper.class, new RequestWrapper.RequestWrapperSerializer())
                .create();
        return gson.toJson(wrapper);
    }
}
