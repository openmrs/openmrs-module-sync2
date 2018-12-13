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
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncValidationException;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Sync2ModuleController {

	protected static final Logger LOGGER = LoggerFactory.getLogger(Sync2ModuleController.class);
	private static final String USER_MODEL = "user";
	private static final String ALERT_MESSAGE_MODEL = "alertMessage";
	private static final String SUCCESS_MESSAGE = "success";
	private static final String PUSH_SUCCESS_MESSAGE = "sync2.sync.push.success";
	private static final String PUSH_FAILURE_MESSAGE = "sync2.sync.push.failure";
	private static final String PULL_SUCCESS_MESSAGE = "sync2.sync.pull.success";
	private static final String PULL_FAILURE_MESSAGE = "sync2.sync.pull.failure";

	@RequestMapping(value = "/module/sync2/sync2")
	public void manage(ModelMap model) {
		model.addAttribute(USER_MODEL, Context.getAuthenticatedUser());
	}

	@RequestMapping(value = "/module/sync2/manualPush")
	public String manualPush(ModelMap model) {
		try {
			LOGGER.info("Start Local Feed Reader...");
			LocalFeedReader localFeedReader = ContextUtils.getLocalFeedReader();
			localFeedReader.readAndPushAllFeeds();
			model.put(SUCCESS_MESSAGE, true);
			model.put(ALERT_MESSAGE_MODEL, PUSH_SUCCESS_MESSAGE);
		} catch (SyncValidationException e) {
			LOGGER.error("Error during reading feeds: ", e);
			model.put(SUCCESS_MESSAGE, false);
			model.put(ALERT_MESSAGE_MODEL, getFirstErrorCode(e.getErrors()));
		} catch (Exception e) {
			LOGGER.error("Error during pushing objects: ", e);
			model.put(SUCCESS_MESSAGE, false);
			model.put(ALERT_MESSAGE_MODEL, PUSH_FAILURE_MESSAGE);
		}
		return "/module/sync2/sync2";
	}

	@RequestMapping(value = "/module/sync2/manualPull")
	public String manualPull(ModelMap model) {
		try {
			LOGGER.info("Start Parent Feed Reader...");
			ParentFeedReader parentFeedReader = ContextUtils.getParentFeedReader();
			parentFeedReader.pullAndProcessAllFeeds();
			model.put(SUCCESS_MESSAGE, true);
			model.put(ALERT_MESSAGE_MODEL, PULL_SUCCESS_MESSAGE);
		} catch (SyncValidationException e) {
			LOGGER.error("Error during reading feeds: ", e);
			model.put(SUCCESS_MESSAGE, false);
			model.put(ALERT_MESSAGE_MODEL, getFirstErrorCode(e.getErrors()));
		} catch (Exception e) {
			LOGGER.error("Error during reading feeds: ", e);
			model.put(SUCCESS_MESSAGE, false);
			model.put(ALERT_MESSAGE_MODEL, PULL_FAILURE_MESSAGE);
		}
		return "/module/sync2/sync2";
	}

	@RequestMapping(value = "/module/sync2/auditList")
	public String auditList() {
		return "/module/sync2/sync2AuditList";
	}

	@RequestMapping(value = "/module/sync2/configuration")
	public String configuration() {
		return "/module/sync2/sync2Configuration";
	}

	private String getFirstErrorCode(Errors errors) {
		return errors.getErrorsCodes().get(SyncConstants.ZERO);
	}
}
