package org.openmrs.module.sync2.fragment.controller;

import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class AuditDetailsFragmentController {

    private static final String AUDIT_LOG = "auditLog";

    private static final String LOCAL_INSTANCE_ID = "localInstanceId";

    public void controller(FragmentModel model,
                           @SpringBean("syncAuditService") SyncAuditService syncAuditService,
                           @FragmentParam(value = "messageUuid", required = true) String messageUuid){
        AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);

        model.addAttribute(AUDIT_LOG, message);
        model.addAttribute(LOCAL_INSTANCE_ID, SyncUtils.getLocalInstanceId());
    }

}
