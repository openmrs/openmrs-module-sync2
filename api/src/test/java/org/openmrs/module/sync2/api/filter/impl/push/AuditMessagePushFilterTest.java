package org.openmrs.module.sync2.api.filter.impl.push;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

public class AuditMessagePushFilterTest {

    private static final String SAMPLE_ACTION = "action";

    private AuditMessagePushFilter auditMessagePushFilter = new AuditMessagePushFilter();

    //TODO handle visits, encounters and obs
    @Test
    public void shouldObjectBeSynced_shouldReturnTrueIfEntryDoesNotDescribeAuditMessage() {
        boolean fetched = auditMessagePushFilter.shouldObjectBeSynced(prepareDummyAuditMessage(
                SyncCategoryConstants.CATEGORY_PATIENT), SAMPLE_ACTION);
        Assert.assertTrue(fetched);
    }

    @Test
    public void shouldObjectBeSynced_shouldReturnFalseIfEntryDescribesAuditMessage() {
        boolean fetched = auditMessagePushFilter.shouldObjectBeSynced(prepareDummyAuditMessage(
                SyncCategoryConstants.CATEGORY_AUDIT_MESSAGE), SAMPLE_ACTION);
        Assert.assertFalse(fetched);
    }

    private AuditMessage prepareDummyAuditMessage(String resourceName) {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setResourceName(resourceName);
        return auditMessage;
    }
}