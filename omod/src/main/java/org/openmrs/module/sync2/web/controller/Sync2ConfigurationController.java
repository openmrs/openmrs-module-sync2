package org.openmrs.module.sync2.web.controller;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.sync2.SyncMessageUtils;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonString;

/**
 * The Sync 2 configuration controller.
 */
@Controller
public class Sync2ConfigurationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2ConfigurationController.class);

	private static final String CONFIGURATION_MODEL = "configuration";
	private static final String SAVE_CONFIG_ERROR = "sync2.configuration.json.save.fail";
	private static final String SAVE_CONFIG_SUCCESS = "sync2.configuration.json.save.success";
	private static final String ERRORS_INVALID_FILE = "sync2.configuration.errors.invalidFile";

	@Autowired
	private SyncConfigurationService syncConfigurationService;

	/**
	 * Sets the UI model attributes used to display the sync configuration and etc.
	 *
	 * @param model injected the page model object
	 * @param success injected the flag used to choose the type of alert message
	 * @param alertMessage injected the message used to display an alert message (display the alert if the value isn't null)
	 * @return the redirect URL to sync2Configuration.jsp
	 */
	@RequestMapping(value = "/module/sync2/configuration")
	public String get(ModelMap model,
			@RequestParam(value = SyncMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = SyncMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage) {
		String configuration =
				writeSyncConfigurationToJsonString(syncConfigurationService.getSyncConfiguration());
		model.addAttribute(CONFIGURATION_MODEL, configuration);
		model.addAttribute(SyncMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(SyncMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);
		return "/module/sync2/sync2Configuration";
	}

	/**
	 * Saves the configuration (sent as JSON string) into the server.
	 * Notifies the user about the result of this operation.
	 *
	 * @param model injected the page model object
	 * @param json injected the JSON representation of the configuration
	 * @return the redirect URL (if success to /module/sync2/sync2 if not then /module/sync2/configuration)
	 */
	@RequestMapping(value = "/module/sync2/saveConfiguration", method = RequestMethod.POST)
	public String post(ModelMap model,
			@RequestParam("json") String json) {
		try {
			syncConfigurationService.saveConfiguration(json);
			SyncMessageUtils.successMessage(model, SAVE_CONFIG_SUCCESS);
			return "/module/sync2/sync2";
		} catch (Exception e) {
			LOGGER.warn("Error during save:", e);
			SyncMessageUtils.errorMessage(model, SAVE_CONFIG_ERROR);
		}
		return "redirect:/module/sync2/configuration.form";
	}

	/**
	 * Verifies if the sent string has valid JSON representation.
	 *
	 * @param json injected the JSON representation of the configuration
	 * @return the Map which contains information about the result of validation,
	 *  used by UI to display an appropriate message
	 */
	@ResponseBody
	@RequestMapping("/module/sync2/verifyJson")
	public Map<String, Boolean>  verifyJson(@RequestParam("json") String json) throws SyncException {
		Map<String, Boolean> result = new HashMap<>();

		if (SyncConfigurationUtils.isValidateJson(json)) {
			result.put("isValid", true);
		} else  {
			LOGGER.warn("Invalid json.");
			result.put("isValid", false);
		}
		return result;
	}

	/**
	 * Saves the configuration (sent as JSON file) into the server.
	 * Notifies the user about the result of this operation.
	 *
	 * @param file injected the JSON configuration file
	 * @param model injected the page model object
	 * @return the redirect URL (if success to /module/sync2/sync2 if not then /module/sync2/configuration)
	 */
	@RequestMapping(value = "/module/sync2/importSyncConfiguration", method = RequestMethod.POST)
	public String saveConfiguration(@RequestParam(value = "file") MultipartFile file,
			ModelMap model) throws SyncException, IOException {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			IOUtils.copy(file.getInputStream(), writer, "UTF-8");
			String jsonContent = writer.toString();
			if (!SyncConfigurationUtils.isValidateJson(jsonContent)) {
				SyncMessageUtils.errorMessage(model, ERRORS_INVALID_FILE);
				return "redirect:/module/sync2/configuration.form";
			}
			SyncConfiguration syncConfiguration = parseJsonStringToSyncConfiguration(jsonContent);
			syncConfigurationService.saveConfiguration(syncConfiguration);
			SyncMessageUtils.successMessage(model, SAVE_CONFIG_SUCCESS);
			return "/module/sync2/sync2";
		} catch (SyncException e) {
			LOGGER.warn("Error during import configuration:", e);
			SyncMessageUtils.errorMessage(model, ERRORS_INVALID_FILE);
		} finally {
			IOUtils.closeQuietly(writer);
		}
		return "redirect:/module/sync2/configuration.form";
	}
}
