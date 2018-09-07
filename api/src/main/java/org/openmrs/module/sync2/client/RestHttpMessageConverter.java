package org.openmrs.module.sync2.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

public class RestHttpMessageConverter extends AbstractHttpMessageConverter<RestResource> {

    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "application";
    private static final String SUBTYPE = "json";
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private final Gson defaultJsonParser;

    private ConversionService conversionService;

    public RestHttpMessageConverter() {
        super(new MediaType(TYPE, SUBTYPE, Charset.forName(CHARSET)));
        conversionService = ContextUtils.getConversionService();
        defaultJsonParser = createDefaultGson();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return RestResource.class.isAssignableFrom(clazz);
    }

    @Override
    protected RestResource readInternal(Class<? extends RestResource> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            String json = IOUtils.toString(inputMessage.getBody());
            return convertJsonToGivenClass(json, clazz);
        } catch (IOException e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
    }

    @Override
    protected void writeInternal(RestResource restResource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            String json = convertRestResourceToJson(restResource);
            outputMessage.getBody().write(json.getBytes());
        } catch (IOException e) {
            throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
        }
    }

    private RestResource convertJsonToGivenClass(String json, Class<? extends RestResource> clazz) {
        if (conversionService.canConvert(String.class, clazz)) {
            return conversionService.convert(json, clazz);
        } else {
            return defaultJsonParser.fromJson(json, clazz);
        }
    }

    private String convertRestResourceToJson(RestResource restResource) {
        if (conversionService.canConvert(restResource.getClass(), String.class)) {
            return conversionService.convert(restResource, String.class);
        } else {
            return defaultJsonParser.toJson(restResource);
        }
    }

    /**
     * This method configures Gson.
     * We need to use workaround for null dates.
     * @return definitive null safe Gson instance
     */
    private Gson createDefaultGson() {
        // Trick to get the DefaultDateTypeAdatpter instance
        // Create a first Gson instance
        Gson gson = new GsonBuilder()
                .setDateFormat(ISO_8601)
                .create();

        // Get the date adapter
        TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);

        // Ensure the DateTypeAdapter is null safe
        TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();

        // Build the definitive safe Gson instance
        return new GsonBuilder()
                .setDateFormat(ISO_8601)
                .registerTypeAdapter(Date.class, safeDateTypeAdapter)
                .create();
    }
}