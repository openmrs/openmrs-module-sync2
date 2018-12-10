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
import org.openmrs.module.sync2.api.exceptions.SyncValidationException;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class Sync2ModuleController {

	protected static final Logger LOGGER = LoggerFactory.getLogger(Sync2ModuleController.class);

	@RequestMapping(value = "/module/sync2/sync2", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}

	@RequestMapping(value = "/module/sync2/manualPush")
	public String manualPush() {
		try {
			LOGGER.info("Start Local Feed Reader...");
			LocalFeedReader localFeedReader = ContextUtils.getLocalFeedReader();
			localFeedReader.readAndPushAllFeeds();
		} catch (SyncValidationException e) {
			LOGGER.error("Error during pushing objects: ", e);
		} catch (Exception e) {
			LOGGER.error("Error during pushing objects: ", e);
		}
		return "module/admin";
	}

	@RequestMapping(value = "/module/sync2/manualPull")
	public String manualPull() {
		try {
			LOGGER.info("Start Parent Feed Reader...");
			ParentFeedReader parentFeedReader = ContextUtils.getParentFeedReader();
			parentFeedReader.pullAndProcessAllFeeds();
		} catch (SyncValidationException e) {
			LOGGER.error("Error during reading feeds: ", e);
		} catch (Exception e) {
			LOGGER.error("Error during reading feeds: ", e);
		}
		return "module/admin";
	}
}
