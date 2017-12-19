package org.openmrs.module.sync2.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import org.openmrs.module.sync2.client.rest.resource.Location;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Privilege;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RestHttpMessageConverter extends AbstractHttpMessageConverter<RestResource> {

    private static final String CHARSET = "UTF-8";
    private static final String TYPE = "application";
    private static final String SUBTYPE = "json";
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private final Set<Class<?>> supportedClasses;
    private final Gson jsonParser;

    public RestHttpMessageConverter() {
        super(new MediaType(TYPE, SUBTYPE, Charset.forName(CHARSET)));
        supportedClasses = new HashSet<>();
        supportedClasses.addAll(Arrays.asList(Patient.class, Privilege.class, Location.class));
        jsonParser = getGson();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return supportedClasses.contains(clazz);
    }

    @Override
    protected RestResource readInternal(Class<? extends RestResource> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            String json = convertStreamToString(inputMessage.getBody());
            return jsonParser.fromJson(json, clazz);
        } catch (IOException e) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
    }

    @Override
    protected void writeInternal(RestResource restResource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            String json = jsonParser.toJson(restResource);
            outputMessage.getBody().write(json.getBytes());
        }
        catch (IOException e) {
            throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }

                reader.close();
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    /**
     * This method configures Gson.
     * We need to use workaround for null dates.
     * @return definitive null safe Gson instance
     */
    private Gson getGson() {
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
