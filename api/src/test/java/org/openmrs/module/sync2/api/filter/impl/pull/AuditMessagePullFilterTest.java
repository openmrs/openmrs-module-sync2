package org.openmrs.module.sync2.api.filter.impl.pull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.sync2.SyncCategoryConstants;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

@RunWith(MockitoJUnitRunner.class)
public class AuditMessagePullFilterTest {

    private static final String EXISTING_ENTRY_UUID = "uuid1";

    private static final String NEW_ENTRY_UUID2 = "uuid2";

    private static final String SAMPLE_ACTION = "action";

    @Mock
    private SyncAuditService syncAuditService;

    @InjectMocks
    private AuditMessagePullFilter auditMessagePullFilter;

    @Before
    public void setUp() {
        Mockito.when(syncAuditService.getMessageByUuid(EXISTING_ENTRY_UUID))
                .thenReturn(prepareDummyAuditMessage(EXISTING_ENTRY_UUID));
    }

    @Test
    public void shouldObjectBeSynced_shouldReturnTrueIfEntryAlreadyExists() {
        boolean fetched = auditMessagePullFilter.shouldObjectBeSynced(prepareDummyAuditMessage(
                EXISTING_ENTRY_UUID), SAMPLE_ACTION);
        Assert.assertTrue(fetched);
    }

    @Test
    public void shouldObjectBeSynced_shouldReturnFalseIfEntryDoesNotExist() {
        boolean fetched = auditMessagePullFilter.shouldObjectBeSynced(prepareDummyAuditMessage(
                NEW_ENTRY_UUID2), SAMPLE_ACTION);
        Assert.assertFalse(fetched);
    }

    private AuditMessage prepareDummyAuditMessage(String uuid) {
        AuditMessage auditMessage = new AuditMessage();
        auditMessage.setUuid(uuid);
        return auditMessage;
    }
}