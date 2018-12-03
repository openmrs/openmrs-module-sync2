package org.openmrs.module.sync2.web.controller.rest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller("sync2.SyncConflictRestController")
@RequestMapping(value = "/rest/sync2/conflict", produces = MediaType.APPLICATION_JSON_VALUE)
public class SyncConflictRestController {

	@Autowired
	private MergeConflictService mergeConflictService;

	@Autowired
	private MergeConflictMapper mergeConflictMapper;

	@RequestMapping(value = "/resolve", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> resolve(@RequestParam("conflictUuid") String conflictUuid,
			@RequestBody String json) {
		org.openmrs.module.sync2.api.model.MergeConflict mergeConflictDAO = mergeConflictService.getByUuid(conflictUuid);
		MergeConflict mergeConflict = mergeConflictMapper.map(mergeConflictDAO);
		try {
			SimpleObject elo = SimpleObject.parseJson(json);
			return ResponseEntity.accepted().body(elo.toString());
		}
		catch (IOException e) {
			return new ResponseEntity<>("Incorrect JSON given\n"
					+ ExceptionUtils.getFullStackTrace(e), HttpStatus.BAD_REQUEST);
		}
	}
}
