package org.openmrs.module.sync2.web.controller;

import org.openmrs.module.sync2.SyncMessageUtils;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/sync2")
public class Sync2AuditDetailsController {

	private static final String MODEL_MESSAGE_UUID = "messageUuid";
	private static final String AUDIT_LOG = "auditLog";
	private static final String LOCAL_INSTANCE_ID = "localInstanceId";

	@Autowired
	private SyncAuditService syncAuditService;

	@RequestMapping(value = "/auditDetails")
	public String get(ModelMap model,
			@RequestParam(value = SyncMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = SyncMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage,
			@RequestParam(value = MODEL_MESSAGE_UUID, required = true) String messageUuid) {

		AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);

		model.addAttribute(AUDIT_LOG, message);
		model.addAttribute(LOCAL_INSTANCE_ID, SyncUtils.getLocalInstanceId());
		model.addAttribute(SyncMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(SyncMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);
		return "/module/sync2/sync2AuditDetails";
	}

}
