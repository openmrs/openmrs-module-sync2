package org.openmrs.module.sync2.web.controller;

import org.openmrs.module.sync2.SyncMessageUtils;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@RequestMapping("/module/sync2")
public class Sync2RetryController {

	private static final String BACK_PAGE_INDEX = "backPageIndex";

	private static final String RETRY_SUCCESS = "sync2.audit.retry.success";

	private static final String RETRY_FAILED = "sync2.audit.retry.failed";

	private static final String RETRY_LOG_UUID = "retryLogUuid";

	private static final String AUDIT_PAGE = "/module/sync2/auditDetails.form";

	private static final String AUDIT_LIST_PAGE = "/module/sync2/auditList.form";

	@Autowired
	private SyncAuditService syncAuditService;

	@Autowired
	private SyncRetryService syncRetryService;

	@RequestMapping(value = "/retry", method = RequestMethod.GET)
	public String retry(ModelMap model,
			@RequestParam(value = RETRY_LOG_UUID) String messageUuid,
			@RequestParam(value = BACK_PAGE_INDEX, required = false) Integer backPageIndex)  {

		AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);
		message = syncRetryService.retryMessage(message);

		String resultUrl = null;
		if (message != null && message.getSuccess()) {
			SyncMessageUtils.successMessage(model, RETRY_SUCCESS);
			resultUrl = buildAuditListUri(backPageIndex);
		} else if (message != null && !message.getSuccess()) {
			SyncMessageUtils.errorMessage(model, RETRY_FAILED);
			resultUrl = buildNewMessageUri(backPageIndex, message);
		}

		return "redirect:" + resultUrl;
	}

	private String buildAuditListUri(Integer backPageIndex) {
		if (backPageIndex == null) {
			backPageIndex = 1;
		}
		return AUDIT_LIST_PAGE + "?pageIndex=" + backPageIndex;
	}

	private String buildNewMessageUri(Integer backPageIndex, AuditMessage message) {
		if (backPageIndex == null) {
			backPageIndex = 1;
		}
		return AUDIT_PAGE + "?messageUuid=" + message.getUuid() + "&backPageIndex=" + backPageIndex;
	}
}
