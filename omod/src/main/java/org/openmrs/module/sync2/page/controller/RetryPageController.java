package org.openmrs.module.sync2.page.controller;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncRetryService;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class RetryPageController {
	private static final String BACK_PAGE_INDEX = "backPageIndex";

	private static final String RETRY_SUCCESS = "sync2.audit.retry.success";

	private static final String RETRY_FAILED = "sync2.audit.retry.failed";

	private static final String RETRY_LOG_UUID = "retryLogUuid";

	private static final String AUDIT_PAGE = "/sync2/details.page";

	private static final String AUDIT_LIST_PAGE = "/sync2/auditList.page";

	public String controller(@RequestParam(value = RETRY_LOG_UUID) String messageUuid,
			@RequestParam(value = BACK_PAGE_INDEX, required = false) Integer backPageIndex,
			@SpringBean("syncAuditService") SyncAuditService syncAuditService,
			@SpringBean("sync2.SyncRetryService") SyncRetryService syncRetryService,
			HttpSession session, UiUtils ui)  {
		AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);
		message = syncRetryService.retryMessage(message);

		String resultUrl = null;
		if (message != null && message.getSuccess()) {
			InfoErrorMessageUtil.flashInfoMessage(session, ui.message(RETRY_SUCCESS));
			resultUrl = buildAuditListUri(backPageIndex);
		} else if (message != null && !message.getSuccess()) {
			InfoErrorMessageUtil.flashErrorMessage(session, ui.message(RETRY_FAILED));
			resultUrl = buildNewMessageUri(backPageIndex, message);
		}

		return "redirect:" + resultUrl;
	}

	private String buildAuditListUri(Integer backPageIndex) {
		return AUDIT_LIST_PAGE + "?pageIndex=" + (backPageIndex == null ? 1 : backPageIndex);
	}

	private String buildNewMessageUri(Integer backPageIndex, AuditMessage message) {
		return AUDIT_PAGE + "?messageUuid=" + message.getUuid() + "&backPageIndex=" +
				(backPageIndex == null ? 1 : backPageIndex);
	}
}
