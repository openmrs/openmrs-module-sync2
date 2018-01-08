package org.openmrs.module.sync2.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.sync2.api.SyncAuditService;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.SyncPushService;
import org.openmrs.module.sync2.api.SyncRetryService;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;

@Component("sync2.SyncRetryService")
public class SyncRetryServiceImpl implements SyncRetryService {

    @Autowired
    private SyncPullService syncPullService;

    @Autowired
    private SyncPushService syncPushService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private SyncConfigurationService configuration;

    @Override
    public AuditMessage retryMessage(AuditMessage message) throws APIException {
        switch(message.getOperation()) {
            case PULL_OPERATION:
                return retryPull(message);
            case PUSH_OPERATION:
                return retryPush(message);
        }
        return null;
    }

    private AuditMessage retryPush(AuditMessage message) {
        AuditMessage newMessage =
                syncPushService.readDataAndPushToParent(
                        message.getResourceName(),
                        message.getAvailableResourceUrlsAsMap(),
                        message.getAction(),
                        message.getLinkType()
                );

        syncAuditService.setNextAudit(message, newMessage);
        return newMessage;
    }

    private AuditMessage retryPull(AuditMessage message) {
        AuditMessage newMessage =
                syncPullService.pullDataFromParentAndSave(
                        message.getResourceName(),
                        message.getAvailableResourceUrlsAsMap(),
                        message.getAction(),
                        message.getLinkType()
                );

        syncAuditService.setNextAudit(message, newMessage);
        return newMessage;
    }
}
