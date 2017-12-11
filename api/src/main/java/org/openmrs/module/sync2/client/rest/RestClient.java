package org.openmrs.module.sync2.client.rest;

import org.openmrs.OpenmrsData;
import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.client.RestHttpMessageConverter;
import org.openmrs.module.sync2.client.RestResourceCreationUtil;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class RestClient implements Client {

    private static final String PATIENT_CATEGORY = "patient";

    private RestTemplate restTemplate = new RestTemplate();

    public RestClient(ClientHttpRequestFactory clientHttpRequestFactory) {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]
                { new RestHttpMessageConverter(), new StringHttpMessageConverter() }));
    }

    @Override
    public Object getObject(String category, String url, String username, String password)
            throws RestClientException {
        restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password)));

        RestResource restResource = (RestResource) restTemplate.getForObject(url, resolveCategory(category));
        return restResource.getOpenMrsObject();
    }

    @Override
    public ResponseEntity<String> postObject(String url, String username, String password, Object object)
            throws RestClientException {
        restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password)));

        RestResource restResource = RestResourceCreationUtil.createRestResourceFromOpenMRSData((OpenmrsData) object);
        return restTemplate.postForEntity(url, restResource, String.class);
    }

    private Class resolveCategory(String category) {
        if (category.equals(PATIENT_CATEGORY)) {
            return Patient.class;
        } else {
            return null;
        }
    }
}
