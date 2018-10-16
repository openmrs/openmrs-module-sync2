package org.openmrs.module.sync2.api.sync;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.sync2.client.ClientHelperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.openmrs.module.sync2.SyncConstants.ACTION_CREATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_UPDATED;
import static org.openmrs.module.sync2.SyncConstants.ACTION_VOIDED;
import static org.openmrs.module.sync2.SyncConstants.LOCAL_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.LOCAL_USERNAME_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.PARENT_USERNAME_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.SYNC2_REST_ENDPOINT;
import static org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance.PARENT;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getParentBaseUrl;
import static org.openmrs.module.sync2.api.utils.SyncUtils.getSyncConfigurationService;

public class SyncClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncClient.class);

	private String username;

	private String password;

	private RestTemplate restTemplate = new RestTemplate();

	public Object pullData(String category, String clientName, String resourceUrl, OpenMRSSyncInstance instance) {
		Object result = null;
		setUpCredentials(instance);

		ClientHelper clientHelper = new ClientHelperFactory().createClient(clientName);
		prepareRestTemplate(clientHelper);

		try {
			result = retrieveObject(category, resourceUrl, clientHelper);
		}
		catch (HttpClientErrorException e) {
			if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw new SyncException("Error during reading local object: ", e);
			}
		}
		catch (URISyntaxException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	public ResponseEntity<String> pushData(Object object, String clientName,
			String resourceUrl, String action, OpenMRSSyncInstance instance) {
		ResponseEntity<String> result = null;
		setUpCredentials(instance);

		ClientHelper clientHelper = new ClientHelperFactory().createClient(clientName);
		prepareRestTemplate(clientHelper);

		try {
			switch (action) {
				case ACTION_CREATED:
					result = createObject(resourceUrl, object, clientHelper);
					break;
				case ACTION_UPDATED:
					result = updateObject(resourceUrl, object, clientHelper);
					break;
				case ACTION_VOIDED:
					result = deleteObject(resourceUrl, (String) object, clientHelper);
					break;
				default:
					LOGGER.warn(String.format("Sync push exception. Unrecognized action: %s", action));
					break;
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new SyncException(String.format("Object posting error. Code: %d. Details: \n%s",
					e.getStatusCode().value(), e.getResponseBodyAsString()), e);
		} catch (URISyntaxException e) {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	private void setUpCredentials(OpenMRSSyncInstance instance) {
		AdministrationService adminService = Context.getAdministrationService();

		this.username = instance.equals(PARENT) ? adminService.getGlobalProperty(PARENT_USERNAME_PROPERTY) :
				adminService.getGlobalProperty(LOCAL_USERNAME_PROPERTY);

		this.password = instance.equals(PARENT) ? adminService.getGlobalProperty(PARENT_PASSWORD_PROPERTY) :
				adminService.getGlobalProperty(LOCAL_PASSWORD_PROPERTY);
	}

	private void prepareRestTemplate(ClientHelper clientHelper) {
		restTemplate.setInterceptors(clientHelper.getCustomInterceptors(this.username, this.password));
		restTemplate.setMessageConverters(clientHelper.getCustomFHIRMessageConverter());
	}

	private Object retrieveObject(String category, String url, ClientHelper clientHelper)
			throws RestClientException, URISyntaxException {
		RequestWrapper requestWrapper = createWrappedRequest(clientHelper.retrieveRequest(url));
		RequestEntity request = new RequestEntity(requestWrapper, HttpMethod.GET, getParentUri());
		return restTemplate.exchange(request, clientHelper.resolveCategoryByCategory(category)).getBody();
	}

	private ResponseEntity<String> createObject(String url, Object object, ClientHelper clientHelper)
			throws RestClientException, URISyntaxException {
		RequestWrapper requestWrapper = createWrappedRequest(clientHelper.createRequest(url, object));
		RequestEntity request = new RequestEntity(requestWrapper, HttpMethod.POST, getParentUri());
		return restTemplate.exchange(request, String.class);
	}

	private ResponseEntity<String> deleteObject(String url, String uuid, ClientHelper clientHelper)
			throws URISyntaxException {
		RequestWrapper requestWrapper = createWrappedRequest(clientHelper.deleteRequest(url, uuid));
		RequestEntity request = new RequestEntity(requestWrapper, HttpMethod.DELETE, getParentUri());
		return restTemplate.exchange(request, String.class);
	}

	private ResponseEntity<String> updateObject(String url, Object object, ClientHelper clientHelper)
			throws URISyntaxException {
		RequestWrapper requestWrapper = createWrappedRequest(clientHelper.updateRequest(url, object));
		RequestEntity request = new RequestEntity(requestWrapper, HttpMethod.PUT, getParentUri());
		return restTemplate.exchange(request, String.class);
	}

	private URI getParentUri() throws URISyntaxException {
		String uri = getParentBaseUrl();
		return new URI(uri + SYNC2_REST_ENDPOINT);
	}

	private RequestWrapper createWrappedRequest(RequestEntity requestEntity) {
		String instanceId = getSyncConfigurationService().getSyncConfiguration().getGeneral().getLocalInstanceId();
		RequestWrapper requestWrapper = new RequestWrapper();
		requestWrapper.setInstanceId(instanceId);
		requestWrapper.setRequestEntity(requestEntity);
		return requestWrapper;
	}
}
