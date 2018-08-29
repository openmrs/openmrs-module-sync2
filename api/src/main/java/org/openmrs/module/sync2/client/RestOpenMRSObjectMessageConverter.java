package org.openmrs.module.sync2.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.openmrs.OpenmrsObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

public class RestOpenMRSObjectMessageConverter extends AbstractHttpMessageConverter<OpenmrsObject> {

  private static final String CHARSET = "UTF-8";
  private static final String TYPE = "application";
  private static final String SUBTYPE = "json";
  private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  private final Gson defaultJsonParser;

  public RestOpenMRSObjectMessageConverter() {
    super(new MediaType(TYPE, SUBTYPE, Charset.forName(CHARSET)));
    defaultJsonParser = createDefaultGson();
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return OpenmrsObject.class.isAssignableFrom(clazz);
  }

  @Override
  protected OpenmrsObject readInternal(Class<? extends OpenmrsObject> clazz, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {
    try {
      String json = IOUtils.toString(inputMessage.getBody());
      return defaultJsonParser.fromJson(json, clazz);
    } catch (IOException e) {
      throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
    }
  }

  @Override
  protected void writeInternal(OpenmrsObject restResource, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    try {
      String json = defaultJsonParser.toJson(restResource);
      outputMessage.getBody().write(json.getBytes());
    } catch (IOException e) {
      throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
    }
  }

  /**
   * This method configures Gson. We need to use workaround for null dates.
   * 
   * @return definitive null safe Gson instance
   */
  private Gson createDefaultGson() {
    Gson gson = new GsonBuilder().setDateFormat(ISO_8601).create();
    TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
    TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();
    return new GsonBuilder().setDateFormat(ISO_8601).registerTypeAdapter(Date.class, safeDateTypeAdapter).create();
  }
}
