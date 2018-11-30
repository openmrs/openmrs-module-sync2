package org.openmrs.module.sync2.web.controller;

import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.openmrs.module.sync2.client.SimpleObjectMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConflictResolutionController {

	@Autowired
	MergeConflictService mergeConflictService;

	private static final String LOCAL_OBJ_MODEL_ATTR = "localObjJson";
	private static final String FOREIGN_OBJ_MODEL_ATTR = "foreignObjJson";
	private static final String CONFLICT_UUID_MODEL_ATTR = "conflictUuid";
	private static final String CLASS_NAME_MODEL_ATTR = "className";


	@RequestMapping(value = "/module/sync2/conflictResolution", method = RequestMethod.GET)
	public void initConflictResolution(
			ModelMap model,
			@RequestParam("conflictUuid") String conflictUuid
	) {
		model.addAttribute(CONFLICT_UUID_MODEL_ATTR, conflictUuid);

		MergeConflict mergeConflict = mergeConflictService.getByUuid(conflictUuid);

		// TODO Fetch real object from mergeConflict object above
		String localObjJson = "{}";
		model.addAttribute(LOCAL_OBJ_MODEL_ATTR, localObjJson);

		String foreignObjJson = "{}";
		model.addAttribute(FOREIGN_OBJ_MODEL_ATTR, foreignObjJson);

		String className = mergeConflict.getFullClassName();
		model.addAttribute(CLASS_NAME_MODEL_ATTR, className);
	}
}
