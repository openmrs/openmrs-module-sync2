package org.openmrs.module.sync2.web.controller;

import org.openmrs.module.sync2.SyncMessageUtils;
import org.openmrs.module.sync2.api.model.enums.Resources;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.api.validator.Errors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping("/module/sync2")
public class Sync2AuditListController {

	private static final String CONFIGURATION_VALIDATION_ERRORS = "configurationValidationErrors";

	private static final String LOCAL_INSTANCE_ID = "localInstanceId";

	private static final String CREATOR_IDS = "creatorIds";

	private static final String RESOURCES_INFO = "resourcesInfo";

	@RequestMapping(value = "/auditList")
	public String get(ModelMap model,
			@RequestParam(value = SyncMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = SyncMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage,
			@RequestParam(value = "backPageIndex", required = false) Integer backPageIndex) {

		model.addAttribute(CONFIGURATION_VALIDATION_ERRORS, getConfigurationValidationErrors());
		model.addAttribute(CREATOR_IDS, getCreatorIdsWithoutOwnId());
		model.addAttribute(LOCAL_INSTANCE_ID, SyncUtils.getLocalInstanceId());
		model.addAttribute(SyncMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(SyncMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);
		model.addAttribute(RESOURCES_INFO, Resources.values());

		model.addAttribute("pageIndex", backPageIndex == null ? 1: backPageIndex);
		return "/module/sync2/sync2AuditList";
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
