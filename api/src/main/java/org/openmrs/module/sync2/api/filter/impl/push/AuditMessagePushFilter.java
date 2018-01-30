package org.openmrs.module.sync2.api.filter.impl.push;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.api.filter.FilterConstant;
import org.openmrs.module.sync2.api.filter.ObjectFilter;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.springframework.stereotype.Component;

@Component(FilterConstant.PUSH_FILTERS_COMPONENT_PREFIX + SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE)
public class AuditMessagePushFilter implements ObjectFilter {

    @Override
    public boolean shouldObjectBeSynced(Object object, String action) {
        AuditMessage auditMessage = (AuditMessage) object;
        return checkIfEntryDoesNotDescribeAuditMessage(auditMessage);
    }

    private boolean checkIfEntryDoesNotDescribeAuditMessage(AuditMessage auditMessage) {
        return !StringUtils.equals(auditMessage.getResourceName(), SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE);
    }
}
