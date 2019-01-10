package org.openmrs.module.sync2.web.controller.rest;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPushService;
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

import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;

@Controller("sync2.SyncConflictRestController")
@RequestMapping(value = "/rest/sync2/conflict", produces = MediaType.APPLICATION_JSON_VALUE)
public class SyncConflictRestController {

	private static final String PAGE = "/openmrs/sync2/details.page?";

	private static final String MESSAGE_UUID = "messageUuid=";

	private static final String BACK_PAGE = "&backPageIndex=1";

	@Autowired
	private SyncAuditService syncAuditService;

	@Autowired
	private SyncPushService syncPushService;

	@RequestMapping(value = "/resolve", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SimpleObject> resolve(@RequestParam("conflictUuid") String conflictUuid,
			@RequestBody String json) {
		AuditMessage conflictMessage = syncAuditService.getMessageByMergeConflictUuid(conflictUuid);
		SimpleObject result = new SimpleObject();
		try {
			SimpleObject entity = SimpleObject.parseJson(json);

			try {
				String uuid = extractUUIDFromResourceLinks(conflictMessage.getAvailableResourceUrlsAsMap());
				AuditMessage resultAudit = syncPushService.mergeForcePush(entity, CategoryEnum.getByCategory(
						conflictMessage.getResourceName()),
						conflictMessage.getAvailableResourceUrlsAsMap(), conflictMessage.getAction(), uuid);
				syncAuditService.setNextAudit(conflictMessage, resultAudit);

				if (resultAudit.getSuccess()) {
					String conflictResolutionUrl = PAGE + MESSAGE_UUID + conflictMessage.getUuid() + BACK_PAGE;
					result.put("url", conflictResolutionUrl);
					return ResponseEntity.accepted().body(result);
				} else {
					return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} catch (Error | Exception e) {
				return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		catch (IOException e) {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}
}
