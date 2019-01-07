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

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.SyncMessageUtils;
import org.openmrs.module.sync2.api.exceptions.SyncValidationException;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Sync 2 module controller.
 */
@Controller
@RequestMapping(value = "/module/sync2")
public class Sync2ModuleController {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2ModuleController.class);

	private static final String USER_MODEL = "user";
	private static final String ATTRIBUTE_EMPTY_URI = "emptyURI";
	private static final String PARENT_URI_ERROR = "sync2.configuration.parentUrl.empty";
	private static final String PUSH_SUCCESS_MESSAGE = "sync2.sync.push.success";
	private static final String PUSH_FAILURE_MESSAGE = "sync2.sync.push.failure";
	private static final String PULL_SUCCESS_MESSAGE = "sync2.sync.pull.success";
	private static final String PULL_FAILURE_MESSAGE = "sync2.sync.pull.failure";

	/**
	 * Sets the UI model attributes used to check if the parent instance URI is valid etc.
	 * Notifies the user if mentioned URI is not valid.
	 *
	 * @param model injected the page model object
	 * @param success injected the flag used to choose the type of alert message
	 * @param alertMessage injected the message used to display an alert message (display the alert if the value isn't null)
	 */
	@RequestMapping(value = "/sync2")
	public void manage(ModelMap model,
			@RequestParam(value = SyncMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = SyncMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage) {
		model.addAttribute(SyncMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(SyncMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);

		boolean emptyURI = parentInstanceUriIsEmpty();
		model.addAttribute(ATTRIBUTE_EMPTY_URI, emptyURI);
		if (emptyURI) {
			SyncMessageUtils.errorMessage(model, PARENT_URI_ERROR);
		}
	}

	/**
	 * Triggers the manual push operation.
	 *
	 * @param model injected the page model object
	 * @return the redirect URL to /module/sync2/sync2
	 */
	@RequestMapping(value = "/manualPush")
	public String manualPush(ModelMap model) {
		try {
			LOGGER.info("Start Local Feed Reader...");
			LocalFeedReader localFeedReader = ContextUtils.getLocalFeedReader();
			localFeedReader.readAndPushAllFeeds();
			SyncMessageUtils.successMessage(model, PUSH_SUCCESS_MESSAGE);
		} catch (SyncValidationException e) {
			LOGGER.error("Error during reading feeds: ", e);
			SyncMessageUtils.errorMessage(model, getFirstErrorCode(e.getErrors()));
		} catch (Exception e) {
			LOGGER.error("Error during pushing objects: ", e);
			SyncMessageUtils.errorMessage(model, PUSH_FAILURE_MESSAGE);
		}
		return "/module/sync2/sync2";
	}

	/**
	 * Triggers the manual pull operation.
	 *
	 * @param model injected the page model object
	 * @return the redirect URL to /module/sync2/sync2
	 */
	@RequestMapping(value = "/manualPull")
	public String manualPull(ModelMap model) {
		try {
			LOGGER.info("Start Parent Feed Reader...");
			ParentFeedReader parentFeedReader = ContextUtils.getParentFeedReader();
			parentFeedReader.pullAndProcessAllFeeds();
			SyncMessageUtils.successMessage(model, PULL_SUCCESS_MESSAGE);
		} catch (SyncValidationException e) {
			LOGGER.error("Error during reading feeds: ", e);
			SyncMessageUtils.errorMessage(model, getFirstErrorCode(e.getErrors()));

		} catch (Exception e) {
			LOGGER.error("Error during reading feeds: ", e);
			SyncMessageUtils.errorMessage(model, PULL_FAILURE_MESSAGE);
		}
		return "/module/sync2/sync2";
	}

	private String getFirstErrorCode(Errors errors) {
		return errors.getErrorsCodes().get(SyncConstants.ZERO);
	}

	private boolean parentInstanceUriIsEmpty() {
		return StringUtils.isBlank(SyncUtils.getParentBaseUrl(null));
	}

}
