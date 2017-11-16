package org.openmrs.module.sync2.client.rest;

import org.openmrs.module.sync2.client.rest.resource.Patient;
import org.openmrs.module.sync2.client.rest.resource.RestResource;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class RestClient {

    // http://localhost:8080/openmrs/ws/rest/v1/patient/a

    private static String address = "http://localhost:8080";
    private static String restPrefix = "/openmrs/ws/rest/v1";

    private static String patientUrl = "/patient/%s?v=full";

    private static String username = "admin";
    private static String password = "Admin123";

    public Object getObject(String id) {

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(Arrays.asList(new BasicAuthInterceptor(username, password)));

        String url = address + restPrefix + String.format(patientUrl, id);


        RestResource restResource = restTemplate.getForObject(url, Patient.class);


        return restResource.getOpenMrsObject();
    }


}
