package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.service.SyncRequestWrapperService;
import org.openmrs.module.sync2.client.ClientHelperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.openmrs.module.sync2.SyncConstants.LOCAL_PASSWORD_PROPERTY;
import static org.openmrs.module.sync2.SyncConstants.LOCAL_USERNAME_PROPERTY;

@Component("sync2.syncRequestWrapperService")
public class SyncRequestWrapperServiceImpl implements SyncRequestWrapperService {

	@Autowired
	private SyncConfigurationService configuration;

	@Override
	public ResponseEntity<String> getObject(RequestWrapper wrapper) {
		RestTemplate restTemplate = prepareRestTemplate(wrapper.getClientName());
		try {
			RequestEntity<String> req = new RequestEntity<>(wrapper.getRequest().getMethod(), wrapper.getRequest().getUrl());
			return copyResponseWithContentType(restTemplate.exchange(req, String.class));
		}
		catch (HttpClientErrorException e) {
			return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
		}
	}

	@Override
	public ResponseEntity<String> sendObject(RequestWrapper wrapper) {
		RestTemplate restTemplate = prepareRestTemplate(wrapper.getClientName());
		try {
			ClientHelper helper = ClientHelperFactory.createClient(wrapper.getClientName());
			Object object = helper.convertToObject(wrapper.getRequest().getBody(), wrapper.getClazz());

			RequestEntity req = new RequestEntity<>(object, wrapper.getRequest().getMethod(), wrapper.getRequest().getUrl());
			ResponseEntity<String> res = restTemplate.exchange(req, String.class);

			return new ResponseEntity<>(res.getBody(), res.getStatusCode());
		}
		catch (HttpClientErrorException e) {
			return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
		}
		catch (ClassNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> deleteObject(RequestWrapper wrapper) {
		RestTemplate restTemplate = prepareRestTemplate(wrapper.getClientName());
		try {
			return restTemplate.exchange(
					wrapper.getRequest().getUrl(),
					wrapper.getRequest().getMethod(),
					new HttpEntity<Object>(wrapper.getRequest().getBody()),
					String.class);
		}
		catch (HttpClientErrorException e) {
			return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
		}
	}

	@Override
	public boolean isRequestAuthenticated(RequestWrapper requestWrapper) {
		User user = Context.getAuthenticatedUser();
		if (user == null)
			return false;

		return isInstanceIdValid(requestWrapper.getInstanceId());
	}

	private boolean isInstanceIdValid(String instanceId) {
		return isWhitelistDisabled() || isInstanceIdOnWhitelist(instanceId);
	}

	private boolean isInstanceIdOnWhitelist(String instanceId) {
		return configuration.getSyncConfiguration().getWhitelist().getInstanceIds().contains(instanceId) &&
				configuration.getSyncConfiguration().getWhitelist().isEnabled();
	}

	private boolean isWhitelistDisabled() {
		return !configuration.getSyncConfiguration().getWhitelist().isEnabled();
	}

	private RestTemplate prepareRestTemplate(String client) {
		RestTemplate restTemplate = new RestTemplate();
		AdministrationService adminService = Context.getAdministrationService();
		String username = adminService.getGlobalProperty(LOCAL_USERNAME_PROPERTY);
		String password = adminService.getGlobalProperty(LOCAL_PASSWORD_PROPERTY);

		ClientHelper helper = ClientHelperFactory.createClient(client);
		restTemplate.setInterceptors(helper.getCustomInterceptors(username, password));
		restTemplate.setMessageConverters(helper.getCustomMessageConverter());

		return restTemplate;
	}

	private ResponseEntity<String> copyResponseWithContentType(ResponseEntity<?> response) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(response.getHeaders().getContentType());
		return new ResponseEntity<>(response.getBody().toString(), headers, response.getStatusCode());
	}
}
