package org.openmrs.module.sync2.client;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.openmrs.module.sync2.api.utils.SyncUtils.createDefaultGson;

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
			throws HttpMessageNotReadableException {
		try {
			String json = IOUtils.toString(inputMessage.getBody());
			return defaultJsonParser.fromJson(json, clazz);
		}
		catch (IOException e) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
		}
	}

	@Override
	protected void writeInternal(SimpleObject simpleObject, HttpOutputMessage outputMessage)
			throws HttpMessageNotWritableException {
		try {
			String json = defaultJsonParser.toJson(simpleObject);
			outputMessage.getBody().write(json.getBytes());
		}
		catch (IOException e) {
			throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
		}
	}

	public SimpleObject convertJsonToGivenClass(String json, Class<? extends SimpleObject> clazz) {
		return defaultJsonParser.fromJson(json, clazz);
	}

	public String convertToJson(SimpleObject simpleObject) {
		return defaultJsonParser.toJson(simpleObject);
	}
}
