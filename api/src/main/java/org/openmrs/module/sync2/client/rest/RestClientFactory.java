package org.openmrs.module.sync2.client.rest;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class RestClientFactory extends HttpComponentsClientHttpRequestFactory {

    private final static int connectionRequestTimeout = 1000;
    private final static int connectTimeout = 15 * 1000;
    private final static int readTimeout = 30 * 1000;

    public RestClientFactory() {
        setConnectionRequestTimeout(connectionRequestTimeout);
        setConnectTimeout(connectTimeout);
        setReadTimeout(readTimeout);
    }
}
