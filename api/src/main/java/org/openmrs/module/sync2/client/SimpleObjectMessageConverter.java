package org.openmrs.module.sync2.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

public class SimpleObjectMessageConverter extends AbstractHttpMessageConverter<SimpleObject> {

  private static final String CHARSET = "UTF-8";
  private static final String TYPE = "application";
  private static final String SUBTYPE = "json";

  private final Gson defaultJsonParser;

  public SimpleObjectMessageConverter() {
    super(new MediaType(TYPE, SUBTYPE, Charset.forName(CHARSET)));
    defaultJsonParser = createDefaultGson();
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return SimpleObject.class.isAssignableFrom(clazz);
  }

  @Override
  protected SimpleObject readInternal(Class<? extends SimpleObject> clazz, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {
    try {
      String json = IOUtils.toString(inputMessage.getBody());
      return defaultJsonParser.fromJson(json, clazz);
    } catch (IOException e) {
      throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
    }
  }

  @Override
  protected void writeInternal(SimpleObject restResource, HttpOutputMessage outputMessage)
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
    Gson gson = new GsonBuilder().setDateFormat(ConversionUtil.DATE_FORMAT).create();
    TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
    TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();
    return new GsonBuilder().setDateFormat(ConversionUtil.DATE_FORMAT).registerTypeAdapter(Date.class, safeDateTypeAdapter).create();
  }
}
