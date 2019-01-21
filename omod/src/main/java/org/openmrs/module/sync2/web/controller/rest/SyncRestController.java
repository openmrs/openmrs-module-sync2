package org.openmrs.module.sync2.web.controller.rest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.sync2.SyncModuleConfig;
import org.openmrs.module.sync2.api.converter.StringToRequestWrapperConverter;
import org.openmrs.module.sync2.api.model.RequestWrapper;
import org.openmrs.module.sync2.api.service.SyncRequestWrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("sync2.SyncRestController")
@RequestMapping(value = "/rest/sync2")
public class SyncRestController {

	private final Logger LOGGER = LoggerFactory.getLogger(SyncRestController.class);

	@Autowired
	private StringToRequestWrapperConverter stringToRequestWrapperConverter;

	@Autowired
	private SyncRequestWrapperService syncRequestWrapperService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> sync(@RequestBody String wrapperJson) {
		RequestWrapper wrapper;
		try {
			wrapper = stringToRequestWrapperConverter.convert(wrapperJson);
		}
		catch (Exception ex) {
			return new ResponseEntity<>("Incorrect AuditMessage JSON given\n"
					+ ExceptionUtils.getFullStackTrace(ex), HttpStatus.BAD_REQUEST);
		}
		LOGGER.debug("Fetched " + wrapper.getRequest().getMethod().toString() + " wrapped request: {}", wrapper);

		if (!syncRequestWrapperService.isRequestAuthenticated(wrapper)) {
			return new ResponseEntity<>(
					String.format("Tried to post %s without '%s' privilege",
							wrapper.getClassName(),
							SyncModuleConfig.MODULE_PRIVILEGE),
					HttpStatus.UNAUTHORIZED);
		}

		return resolveRequest(wrapper);
	}

	private ResponseEntity<String> resolveRequest(RequestWrapper wrapper) {
		HttpMethod method = wrapper.getRequest().getMethod();

		if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT)) {
			return syncRequestWrapperService.sendObject(wrapper);
		} else if (method.equals(HttpMethod.GET)) {
			return syncRequestWrapperService.getObject(wrapper);
		} else if (method.equals(HttpMethod.DELETE)) {
			return syncRequestWrapperService.deleteObject(wrapper);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
