package org.openmrs.module.sync2.web.controller;

import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.sync2.api.mapper.MergeConflictMapper;
import org.openmrs.module.sync2.api.service.MergeConflictService;
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

	@Autowired
	private MergeConflictService mergeConflictService;

	@Autowired
	private MergeConflictMapper mergeConflictMapper;

	private static final SimpleObjectMessageConverter simpleConverter = new SimpleObjectMessageConverter();

	private static final String LOCAL_OBJ_MODEL_ATTR = "localObjJson";
	private static final String FOREIGN_OBJ_MODEL_ATTR = "foreignObjJson";
	private static final String CONFLICT_UUID_MODEL_ATTR = "conflictUuid";
	private static final String CLASS_NAME_MODEL_ATTR = "className";

	@RequestMapping(value = "/module/sync2/conflictResolution", method = RequestMethod.GET)
	public void initConflictResolution(ModelMap model,
			@RequestParam("conflictUuid") String conflictUuid) {
		model.addAttribute(CONFLICT_UUID_MODEL_ATTR, conflictUuid);

		model.addAttribute("conflictUuid", conflictUuid);

		org.openmrs.module.sync2.api.model.MergeConflict mergeConflictDAO = mergeConflictService.getByUuid(conflictUuid);
		MergeConflict mergeConflict = mergeConflictMapper.map(mergeConflictDAO);

		String localObjJson = simpleConverter.convertToJson((SimpleObject) mergeConflict.getOrgLocal());
		String foreignObjJson = simpleConverter.convertToJson((SimpleObject) mergeConflict.getOrgForeign());

		model.addAttribute(LOCAL_OBJ_MODEL_ATTR, localObjJson);
		model.addAttribute(FOREIGN_OBJ_MODEL_ATTR, foreignObjJson);
		model.addAttribute(CLASS_NAME_MODEL_ATTR, mergeConflict.getClazz().getCanonicalName());
	}
}
