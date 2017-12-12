package org.openmrs.module.sync2.fragment.controller;

import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.impl.SyncAuditServiceImpl;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.FragmentRequest;

import java.io.IOException;

public class AuditDetailsFragmentController {

    public void controller(FragmentModel model, FragmentRequest request,
                           @SpringBean("syncAuditService") SyncAuditService syncAuditService,
                           @FragmentParam(value = "messageId", required = true) Integer messageId) throws IOException {
        AuditMessage message = syncAuditService.getMessageById(messageId);

        model.addAttribute("auditLog", message);

        request.setProviderName("sync2");
    }
}