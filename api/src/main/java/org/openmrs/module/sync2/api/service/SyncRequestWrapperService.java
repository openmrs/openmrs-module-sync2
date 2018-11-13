package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.springframework.http.ResponseEntity;

public interface SyncRequestWrapperService {

    ResponseEntity<String> getObject(RequestWrapper wrapper);

    ResponseEntity<String> sendObject(RequestWrapper wrapper);

    ResponseEntity<String> deleteObject(RequestWrapper wrapper);

    boolean isRequestAuthenticated(RequestWrapper requestWrapper);
}
