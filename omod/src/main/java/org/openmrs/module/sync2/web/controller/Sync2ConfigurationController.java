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

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonString;

@Controller
public class Sync2ConfigurationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2ConfigurationController.class);

	private static final String CONFIGURATION_MODEL = "configuration";
	private static final String SAVE_CONFIG_ERROR = "sync2.configuration.json.save.fail";
	private static final String SAVE_CONFIG_SUCCESS = "sync2.configuration.json.save.success";
	private static final String ERRORS_INVALID_FILE = "sync2.configuration.errors.invalidFile";

	@Autowired
	private SyncConfigurationService syncConfigurationService;

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

	@RequestMapping(value = "/module/sync2/saveConfiguration", method = RequestMethod.POST)
	public String post(ModelMap model,
			@RequestParam("json") String json,
			HttpSession session) {
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
