package org.openmrs.module.sync2.client.rest;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class RestClientFactory extends HttpComponentsClientHttpRequestFactory {

    private final static int SECOND = 1000;
    private final static int CONNECTION_REQUEST_TIMEOUT = SECOND;
    private final static int CONNECT_TIMEOUT = 15 * SECOND;
    private final static int READ_TIMEOUT = 30 * SECOND;

    public RestClientFactory() {
        setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT);
        setConnectTimeout(CONNECT_TIMEOUT);
        setReadTimeout(READ_TIMEOUT);
    }
}
