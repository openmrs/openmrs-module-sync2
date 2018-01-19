package org.openmrs.module.sync2.fragment.controller;

import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncRetryService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

public class AuditDetailsFragmentController {

    private static final String RETRY_SUCCESS = "sync2.audit.retry.success";
    private static final String RETRY_FAILED = "sync2.audit.retry.failed";

    public void controller(FragmentModel model, FragmentRequest request,
                           @SpringBean("syncAuditService") SyncAuditService syncAuditService,
                           @FragmentParam(value = "messageUuid", required = true) String messageUuid){
        AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);

        model.addAttribute("auditLog", message);

        request.setProviderName("sync2");
    }

    @RequestMapping(value = "/sync2/retry")
    public SimpleObject retry(@RequestParam(value = "retryLogUuid") String messageUuid,
                              @SpringBean("syncAuditService") SyncAuditService syncAuditService,
                              @SpringBean("sync2.SyncRetryService") SyncRetryService syncRetryService,
                              HttpSession session, UiUtils ui)  {
        AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);
        message = syncRetryService.retryMessage(message);
        SimpleObject result = new SimpleObject();

        if (message != null && message.getSuccess()) {
            InfoErrorMessageUtil.flashInfoMessage(session, ui.message(RETRY_SUCCESS));
            result.put("url", "/sync2/auditList.page");
        } else if (message != null && !message.getSuccess()) {
            InfoErrorMessageUtil.flashInfoMessage(session, ui.message(RETRY_FAILED));
            String newMessageUrl = "/sync2/details.page?messageUuid=" + message.getUuid() + "&backPage=auditList" + "&backPageIndex=" + 1;
            result.put("url", newMessageUrl);
        }

        return result;
    }
}