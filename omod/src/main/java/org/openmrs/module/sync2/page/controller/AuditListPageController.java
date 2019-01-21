package org.openmrs.module.sync2.page.controller;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class AuditListPageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditListPageController.class);

    private static final String CONFIGURATION_VALIDATION_ERRORS = "configurationValidationErrors";

    private static final String LOCAL_INSTANCE_ID = "localInstanceId";

    private static final String CREATOR_IDS = "creatorIds";

    public void controller(PageModel model) {
        model.addAttribute(CONFIGURATION_VALIDATION_ERRORS, getConfigurationValidationErrors());
        model.addAttribute(CREATOR_IDS, getCreatorIdsWithoutOwnId());
        model.addAttribute(LOCAL_INSTANCE_ID, SyncUtils.getLocalInstanceId());
    }

    private Set<String> getCreatorIdsWithoutOwnId() {
        Set<String> creatorIds = getSyncAuditService().getAllCreatorIds();
        creatorIds.remove(SyncUtils.getLocalInstanceId());
        return creatorIds;
    }

    private SyncAuditService getSyncAuditService() {
        return ContextUtils.getFirstRegisteredComponent(SyncAuditService.class);
    }

    private Errors getConfigurationValidationErrors() {
        return SyncUtils.getSyncConfigurationService().validateConfiguration();
    }
}
