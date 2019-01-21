package org.openmrs.module.sync2.api.service.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.helper.CategoryHelper;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.api.service.SyncRetryService;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.sync2.SyncConstants.PULL_OPERATION;
import static org.openmrs.module.sync2.SyncConstants.PUSH_OPERATION;
import static org.openmrs.module.sync2.api.utils.SyncUtils.extractUUIDFromResourceLinks;

@Component("sync2.SyncRetryService")
public class SyncRetryServiceImpl implements SyncRetryService {

    @Autowired
    private SyncPullService syncPullService;

    @Autowired
    private SyncPushService syncPushService;

    @Autowired
    private SyncAuditService syncAuditService;

    @Autowired
    private CategoryHelper categoryHelper;

    @Override
    public AuditMessage retryMessage(AuditMessage message) throws APIException {
        SyncConfigurationUtils.checkIfConfigurationIsValid();
        checkIfCurrentIstanceIsCreatorInstance(message);

        switch (message.getOperation()) {
            case PULL_OPERATION:
                return retryPull(message);
            case PUSH_OPERATION:
                return retryPush(message);
        }
        return null;
    }

    private AuditMessage retryPush(AuditMessage message) {
        String uuid = extractUUIDFromResourceLinks(message.getAvailableResourceUrlsAsMap());

        AuditMessage newMessage =
                syncPushService.readAndPushObjectToParent(
                        categoryHelper.getByCategory(message.getResourceName()),
                        message.getAvailableResourceUrlsAsMap(),
                        message.getAction(),
                        message.getLinkType(),
                        uuid
                );

        syncAuditService.setNextAudit(message, newMessage);
        return newMessage;
    }

    private AuditMessage retryPull(AuditMessage message) {
        String uuid = extractUUIDFromResourceLinks(message.getAvailableResourceUrlsAsMap());

        AuditMessage newMessage =
                syncPullService.pullAndSaveObjectFromParent(
                        categoryHelper.getByCategory(message.getResourceName()),
                        message.getAvailableResourceUrlsAsMap(),
                        message.getAction(),
                        message.getLinkType(),
                        uuid
                );

        syncAuditService.setNextAudit(message, newMessage);
        return newMessage;
    }

    private void checkIfCurrentIstanceIsCreatorInstance(AuditMessage message) {
        String localInstanceId = SyncUtils.getLocalInstanceId();
        if (!StringUtils.equals(message.getCreatorInstanceId(), localInstanceId)) {
            throw new SyncException(String.format("Retry cannot be done. " +
                    "Current instance ID is not equal creator of the message.\n" +
                    "LocalInstanceId=%s\n" +
                    "The AuditMessage's creatorId=%s\n" +
                    "The AuditMessage's UUID=%s\n",
                    localInstanceId, message.getCreatorInstanceId(), message.getUuid()));
        }
    }
}
