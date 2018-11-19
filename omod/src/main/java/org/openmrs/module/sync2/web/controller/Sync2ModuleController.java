/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.sync2.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.api.model.MergeConflict;
import org.openmrs.module.sync2.api.service.MergeConflictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Controller
public class Sync2ModuleController {

	@Autowired
	MergeConflictService mergeConflictService;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(Sync2ModuleController.class);

	@RequestMapping(value = "/module/sync2/sync2", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}

	@RequestMapping(value = "/module/sync2/conflictedObject", method = RequestMethod.GET)
	public void manage2(ModelMap model){model.addAttribute("test","udany");}

	@RequestMapping(value = "/module/sync2/conflictResolution", method = RequestMethod.GET)
	public void initConflictResolution(
			ModelMap model,
			@RequestParam("conflictId") String conflictUuid
	) {
		model.addAttribute("conflictId", conflictUuid);

		//MergeConflictService mergeConflictService = new MergeConflictServiceImpl();
		MergeConflict mergeConflict = mergeConflictService.getByUuid(conflictUuid);
		String className = mergeConflict.getFullClassName();
		/*try {
			Class<?> clazz = Class.forName(className);
			Method[] methods = clazz.getDeclaredMethods();
			for (Method m:
			     methods) {
				m.invoke(orgLocalObject);
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}*/

		model.addAttribute("className", className);
	}
}
