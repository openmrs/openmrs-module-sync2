package org.openmrs.module.sync2.web.controller;

import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.openmrs.module.sync2.api.service.SyncAuditService;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConflictResolutionController {

	private static final String CONFLICT_UUID = "conflictUuid";

	private static final String MESSAGE_UUID = "messageUuid";

	private static final String AUDIT_BACK_PAGE = "auditBackPage";

	private static final String BACK_PAGE_INDEX = "backPageIndex";

	private static final String LOCAL_OBJ_MODEL_ATTR = "localObjJson";

	private static final String FOREIGN_OBJ_MODEL_ATTR = "foreignObjJson";

	private static final String CONFLICT_UUID_MODEL_ATTR = "conflictUuid";

	private static final String CLASS_NAME_MODEL_ATTR = "className";

	private static final SimpleObjectMessageConverter simpleConverter = new SimpleObjectMessageConverter();

	@Autowired
	private MergeConflictService mergeConflictService;

	@Autowired
	private MergeConflictMapper mergeConflictMapper;

	@Autowired
	private SyncAuditService syncAuditService;

	@RequestMapping(value = "/module/sync2/conflictResolution", method = RequestMethod.GET)
	public void initConflictResolution(ModelMap model,
			@RequestParam(value = MESSAGE_UUID) String messageUuid,
			@RequestParam(value = AUDIT_BACK_PAGE, required = false) String auditBackPage,
			@RequestParam(value = BACK_PAGE_INDEX, required = false) Integer backPageIndex) {

		AuditMessage message = syncAuditService.getMessageByUuid(messageUuid);
		String conflictUuid = message.getMergeConflictUuid();

		model.addAttribute(CONFLICT_UUID_MODEL_ATTR, conflictUuid);
		model.addAttribute(CONFLICT_UUID, conflictUuid);
		model.addAttribute(AUDIT_BACK_PAGE, auditBackPage);
		model.addAttribute(BACK_PAGE_INDEX, backPageIndex);

		org.openmrs.module.sync2.api.model.MergeConflict mergeConflictDAO = mergeConflictService.getByUuid(conflictUuid);
		MergeConflict mergeConflict = mergeConflictMapper.map(mergeConflictDAO);

		String localObjJson = simpleConverter.convertToJson((SimpleObject) mergeConflict.getOrgLocal());
		String foreignObjJson = simpleConverter.convertToJson((SimpleObject) mergeConflict.getOrgForeign());

		model.addAttribute(LOCAL_OBJ_MODEL_ATTR, localObjJson);
		model.addAttribute(FOREIGN_OBJ_MODEL_ATTR, foreignObjJson);
		model.addAttribute(CLASS_NAME_MODEL_ATTR, mergeConflict.getClazz().getCanonicalName());
	}
}
