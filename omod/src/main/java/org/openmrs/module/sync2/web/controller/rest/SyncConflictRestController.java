package org.openmrs.module.sync2.web.controller.rest;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.helper.CategoryHelper;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "/rest/sync2/conflict")
public class SyncConflictRestController {

	private static final String DEFAULT_PAGE = "/openmrs/module/sync2/auditDetails.form";

	private static final String MESSAGE_UUID = "messageUuid=";

	private static final String BACK_PAGE_INDEX = "&backPageIndex=";

	@Autowired
	private SyncAuditService syncAuditService;

	@Autowired
	private SyncPushService syncPushService;

	@Autowired
	private CategoryHelper categoryHelper;

	@RequestMapping(value = "/resolve", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SimpleObject> resolve(@RequestParam("conflictUuid") String conflictUuid,
			@RequestParam(value = "auditBackPage", required = false) String auditBackPage,
			@RequestParam(value = "backPageIndex", required = false) Integer backPageIndex,
			@RequestBody String json) {

		AuditMessage conflictMessage = syncAuditService.getMessageByMergeConflictUuid(conflictUuid);
		SimpleObject result = new SimpleObject();
		try {
			SimpleObject entity = SimpleObject.parseJson(json);

			try {
				String uuid = extractUUIDFromResourceLinks(conflictMessage.getAvailableResourceUrlsAsMap(),
						conflictMessage.getResourceName(), SyncConstants.REST_CLIENT);
				AuditMessage resultAudit = syncPushService.mergeForcePush(entity, categoryHelper.getByCategory(
						conflictMessage.getResourceName()),
						conflictMessage.getAvailableResourceUrlsAsMap(), conflictMessage.getAction(), uuid);
				syncAuditService.setNextAudit(conflictMessage, resultAudit);

				if (resultAudit.getSuccess()) {
					String conflictResolutionUrl = buildResolutionUrl(conflictMessage, auditBackPage, backPageIndex);
					result.put("url", conflictResolutionUrl);
					return new ResponseEntity<SimpleObject>(result, HttpStatus.ACCEPTED);
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

	private String buildResolutionUrl(AuditMessage conflictMessage, String auditBackPage, Integer backPageIndex) {
		return (StringUtils.isBlank(auditBackPage) ? DEFAULT_PAGE : auditBackPage) +
				"?" + MESSAGE_UUID + conflictMessage.getUuid() +
				BACK_PAGE_INDEX + (backPageIndex == null ? 1 : backPageIndex);
	}
}
