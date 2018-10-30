package org.openmrs.module.sync2.client.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.client.BasicAuthInterceptor;
import org.openmrs.module.fhir.api.client.HeaderClientHttpRequestInterceptor;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.client.RestHttpMessageConverter;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.sync2.SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE;

public class RESTClientHelper implements ClientHelper {

	public static final String VOIDED = "voided";

	private final RestHttpMessageConverter restConverter = new RestHttpMessageConverter();

	private final SimpleObjectMessageConverter simpleConverter = new SimpleObjectMessageConverter();

	@Override
	public RequestEntity retrieveRequest(String url) throws URISyntaxException {
		return new RequestEntity(HttpMethod.GET, new URI(url));
	}

	@Override
	public RequestEntity createRequest(String url, Object object) throws URISyntaxException {
		if (object instanceof SimpleObject) {
			getRestResourceConverter().convertObject(url, object);
		}

		return new RequestEntity(convertToFormattedData(object), HttpMethod.POST, new URI(url));
	}

	@Override
	public RequestEntity deleteRequest(String url, String uuid) throws URISyntaxException {
		url += "/" + uuid;
		return new RequestEntity(uuid, HttpMethod.DELETE, new URI(url));
	}

	@Override
	public RequestEntity updateRequest(String url, Object object) throws URISyntaxException {
		if (object instanceof AuditMessage) {
			url += "/" + ((AuditMessage) object).getUuid();
			return new RequestEntity(convertToFormattedData(object), HttpMethod.POST, new URI(url));
		} else {
			getRestResourceConverter().convertObject(url, object);
			url += "/" + ((SimpleObject) object).get("uuid");
		}
		return new RequestEntity(convertToFormattedData(object), HttpMethod.POST, new URI(url));
	}

	@Override
	public Class resolveClassByCategory(String category) {
		if (category.equalsIgnoreCase(CATEGORY_AUDIT_MESSAGE)) {
			return AuditMessage.class;
		}
		return SimpleObject.class;
	}

	@Override
	public List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password) {
		return Arrays.asList(new BasicAuthInterceptor(username, password),
				new HeaderClientHttpRequestInterceptor(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));
	}

	@Override
	public List<HttpMessageConverter<?>> getCustomMessageConverter() {
		return Arrays.asList(new HttpMessageConverter<?>[]
				{ new RestHttpMessageConverter(), new StringHttpMessageConverter(),
						new SimpleObjectMessageConverter() });
	}

	@Override
	public boolean compareResourceObjects(String category, Object from, Object dest) {
		boolean result;
		if (category.equals(CATEGORY_AUDIT_MESSAGE)) {
			result = ((AuditMessage) from).getUuid().equals(((AuditMessage) dest).getUuid());
		} else {
			//TODO: Work around for deleting patient through REST API. Should be refactored.
			if (voidedObject((SimpleObject) from) && voidedObject((SimpleObject) dest)) {
				result = true;
			} else {
				result = getRestResourceConverter().deepCompareSimpleObject((SimpleObject) from, (SimpleObject) dest);
			}
		}
		return result;
	}

	@Override
	public Object convertToObject(String formattedData, Class<?> clazz) {
		if (RestResource.class.isAssignableFrom(clazz)) {
			Class<? extends RestResource> restClass = (Class<? extends RestResource>) clazz;
			return restConverter.convertJsonToGivenClass(formattedData, restClass);
		} else if (SimpleObject.class.isAssignableFrom(clazz)) {
			Class<? extends SimpleObject> simpleClass = (Class<? extends SimpleObject>) clazz;
			return simpleConverter.convertJsonToGivenClass(formattedData, simpleClass);
		} else {
			throw new UnsupportedOperationException(getNotSupportedClassMsg(clazz.getCanonicalName()));
		}
	}

	@Override
	public String convertToFormattedData(Object object) {
		if (RestResource.class.isAssignableFrom(object.getClass())) {
			return restConverter.convertToJson((RestResource) object);
		} else if (SimpleObject.class.isAssignableFrom(object.getClass())) {
			return simpleConverter.convertToJson((SimpleObject) object);
		} else {
			throw new UnsupportedOperationException(getNotSupportedClassMsg(object.getClass().getCanonicalName()));
		}
	}

	private RestResourceConverter getRestResourceConverter() {
		return Context.getRegisteredComponent("sync2.RestResourceConverter", RestResourceConverter.class);
	}

	private boolean voidedObject(SimpleObject simpleObject) {
		boolean result = false;
		if (simpleObject.containsKey(VOIDED) && ((boolean) simpleObject.get(VOIDED))) {
			result = true;
		}
		return result;
	}

	private String getNotSupportedClassMsg(String className) {
		return String.format("Class %s is not supported", className);
	}
}
