package org.openmrs.module.sync2.client.rest;

import org.openmrs.module.fhir.api.client.Client;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class RestClient implements Client {

    private static final String PATIENT_CATEGORY = "patient";

    @Override
    public Object getObject(String category, String url, String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password)));
        restTemplate.setRequestFactory(new RestClientFactory());

        RestResource restResource = (RestResource) restTemplate.getForObject(url, resolveCategory(category));

        return restResource.getOpenMrsObject();
    }

    private Class resolveCategory(String category) {
        if (category.equals(PATIENT_CATEGORY)) {
            return Patient.class;
        } else {
            return null;
        }
    }

}
