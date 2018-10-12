package org.openmrs.module.sync2.client.rest;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.fhir.api.client.BasicAuthInterceptor;
import org.openmrs.module.fhir.api.client.HeaderClientHttpRequestInterceptor;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.sync2.api.utils.SyncObjectsUtils;
import org.openmrs.module.sync2.client.RestHttpMessageConverter;
import org.openmrs.module.sync2.client.RestResourceCreationUtil;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class RESTClientHelper implements ClientHelper {

	private static final String ACCEPT_HEADER = "Accept";

	private static final String ACCEPT_MIME_TYPE = "application/json";

	@Override
	public RequestEntity retrieveRequest(String url) throws URISyntaxException {
		return new RequestEntity(HttpMethod.GET, new URI(url));
	}

	@Override
	public RequestEntity createRequest(String url, Object object) throws URISyntaxException {

		//TODO: Hack - we couldn't use response from GET as the POST request
		if (object instanceof RestResource) {
			object = ((RestResource) object).getOpenMrsObject();
		}
		object = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) object);

		return new RequestEntity(object, HttpMethod.POST, new URI(url));
	}

	@Override
	public RequestEntity deleteRequest(String url, String uuid) throws URISyntaxException {
		url += "/" + uuid;
		return new RequestEntity(uuid, HttpMethod.DELETE, new URI(url));
	}

	@Override
	public RequestEntity updateRequest(String url, Object object) throws URISyntaxException {
		//TODO: Hack - we couldn't use response from GET as the POST request
		if (object instanceof RestResource) {
			object = ((RestResource) object).getOpenMrsObject();
		}
		url += "/" + ((OpenmrsObject) object).getUuid();
		object = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) object);
		return new RequestEntity(object, HttpMethod.POST, new URI(url));
	}

	@Override
	public Class resolveCategoryByCategory(String category) {
		return SyncObjectsUtils.getRestClass(category);
	}

	@Override
	public List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password) {
		return Arrays.asList(new BasicAuthInterceptor(username, password),
				new HeaderClientHttpRequestInterceptor(ACCEPT_HEADER, ACCEPT_MIME_TYPE));
	}

	@Override
	public List<HttpMessageConverter<?>> getCustomFHIRMessageConverter() {
		return Arrays.asList(new HttpMessageConverter<?>[]
				{ new RestHttpMessageConverter(), new StringHttpMessageConverter() });
	}
}
