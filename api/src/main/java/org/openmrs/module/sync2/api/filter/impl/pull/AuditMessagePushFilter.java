package org.openmrs.module.sync2.api.filter.impl.pull;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.filter.FilterConstant;
import org.openmrs.module.sync2.api.filter.ObjectFilter;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(FilterConstant.PULL_FILTERS_COMPONENT_PREFIX + SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE)
public class AuditMessagePushFilter implements ObjectFilter {

    @Autowired
    private SyncAuditService syncAuditService;

    @Override
    public boolean shouldObjectBeSynced(Object object, String action) {
        AuditMessage auditMessage = (AuditMessage) object;
        return syncAuditService.getMessageByUuid(auditMessage.getUuid()) != null;
    }
}
