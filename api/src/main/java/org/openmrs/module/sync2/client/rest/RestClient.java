package org.openmrs.module.sync2.client.rest;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.client.RestHttpMessageConverter;
import org.openmrs.module.sync2.client.RestResourceCreationUtil;
import org.openmrs.module.sync2.client.rest.resource.Location;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.Privilege;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;


public class RestClient implements Client {

    private static final String PATIENT_CATEGORY = "patient";
    private static final String LOCATION_CATEGORY = "location";
    private static final String PRIVILEGE_CATEGORY = "privilege";

    private RestTemplate restTemplate = new RestTemplate();

    public RestClient() {
        restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]
                { new RestHttpMessageConverter(), new StringHttpMessageConverter() }));
    }

    @Override
    public Object retrieveObject(String category, String url, String username, String password)
            throws RestClientException {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));

        RestResource restResource = (RestResource) restTemplate.getForObject(url, resolveCategory(category));
        return restResource.getOpenMrsObject();
    }

    @Override
    public ResponseEntity<String> createObject(String url, String username, String password, Object object)
            throws RestClientException {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));

        RestResource restResource = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) object);
        return restTemplate.postForEntity(url, restResource, String.class);
    }

    @Override
    public ResponseEntity<String> deleteObject(String url, String username, String password, String uuid) {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));
        url += "/" + uuid;
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<Object>(uuid), String.class);
    }

    @Override
    public ResponseEntity<String> updateObject(String url, String username, String password, Object object) {
        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthInterceptor(username, password)));

        RestResource restResource = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsObject) object);
        url += "/" + ((OpenmrsObject) object).getUuid();
        return restTemplate.postForEntity(url, restResource, String.class);
    }

    private Class resolveCategory(String category) {
        switch (category) {
            case PATIENT_CATEGORY:
                return Patient.class;
            case LOCATION_CATEGORY:
                return Location.class;
            case PRIVILEGE_CATEGORY:
                return Privilege.class;
            default:
                return null;
        }
    }
}
