package org.openmrs.module.sync2.client;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.openmrs.module.sync2.api.utils.SyncUtils.createDefaultGson;

public class RequestWrapperConverter extends AbstractHttpMessageConverter<RequestWrapper> {

	private static final String CHARSET = "UTF-8";
	private static final String TYPE = "application";
	private static final String SUBTYPE = "json";

	private final Gson defaultJsonParser;

	private ConversionService conversionService;

	public RequestWrapperConverter() {
		super(new MediaType(TYPE, SUBTYPE, Charset.forName(CHARSET)));
		this.defaultJsonParser = createDefaultGson();
		this.conversionService = ContextUtils.getConversionService();
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return RequestWrapper.class.equals(clazz);
	}

	@Override
	protected RequestWrapper readInternal(Class<? extends RequestWrapper> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		try {
			String json = IOUtils.toString(inputMessage.getBody(), "UTF-8");
			return convertJsonToGivenClass(json, clazz);
		} catch (IOException e) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
		}
	}

	@Override
	protected void writeInternal(RequestWrapper requestWrapper, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		try {
			String json = convertRequestWrapperToJson(requestWrapper);
			outputMessage.getBody().write(json.getBytes());
		} catch (IOException e) {
			throw new HttpMessageNotWritableException("Could not serialize object. Msg: " + e.getMessage(), e);
		}
	}

	private RequestWrapper convertJsonToGivenClass(String json, Class<? extends RequestWrapper> clazz) {
		if (conversionService.canConvert(String.class, clazz)) {
			return conversionService.convert(json, clazz);
		} else {
			return defaultJsonParser.fromJson(json, clazz);
		}
	}

	private String convertRequestWrapperToJson(RequestWrapper requestWrapper) {
		if (conversionService.canConvert(requestWrapper.getClass(), String.class)) {
			return conversionService.convert(requestWrapper, String.class);
		} else {
			return defaultJsonParser.toJson(requestWrapper);
		}
	}
}
