package org.openmrs.module.sync2.client.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class RestClient {

    private static final String PATIENT_CATEGORY = "patient";

    public Object getObject(String category, String url) {
        final String username = Context.getAdministrationService().getGlobalProperty("sync2.user.login");
        final String password = Context.getAdministrationService().getGlobalProperty("sync2.user.password");

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password)));

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
