package org.openmrs.module.sync2.web.controller;

import org.apache.commons.io.IOUtils;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.parseJsonStringToSyncConfiguration;
import static org.openmrs.module.sync2.api.utils.SyncConfigurationUtils.writeSyncConfigurationToJsonString;
@Controller
public class Sync2ConfigurationController {

	@Autowired
	private SyncConfigurationService syncConfigurationService;

	@Autowired
	@Qualifier("messageSourceService")
	private MessageSourceService messageSourceService;

	private static final String ALERT_MESSAGE_MODEL = "alertMessage";
	private static final String SUCCESS_MESSAGE = "success";
	private static final String SAVE_CONFIG_ERROR = "sync2.configuration.json.save.fail";
	private static final String SAVE_CONFIG_SUCCESS = "sync2.configuration.json.save.success";

	protected static final Logger LOGGER = LoggerFactory.getLogger(Sync2ConfigurationController.class);

	// this works
	@RequestMapping(value = "/module/sync2/configuration")
	public String get(ModelMap model,
			@RequestParam(value = "importStatus", required = false) String importStatus) {
		String configuration =
				writeSyncConfigurationToJsonString(syncConfigurationService.getSyncConfiguration());
		model.addAttribute("configuration", configuration);
		model.addAttribute("importStatus", importStatus);
		return "/module/sync2/sync2Configuration";
	}

	// this works
	@RequestMapping(value = "/module/sync2/saveConfiguration", method = RequestMethod.POST)
	public String post(ModelMap model,
			@RequestParam("json") String json,
			HttpSession session) {
		try {
			syncConfigurationService.saveConfiguration(json);
			model.put(SUCCESS_MESSAGE, true);
			model.put(ALERT_MESSAGE_MODEL, SAVE_CONFIG_SUCCESS);
			return "/module/sync2/sync2";
		} catch (Exception e) {
			LOGGER.warn("Error during save:", e);
			model.put(SUCCESS_MESSAGE, false);
			model.put(ALERT_MESSAGE_MODEL, SAVE_CONFIG_ERROR);
		}
		return "/module/sync2/sync2Configuration";
	}

	// TODO
	@ResponseBody
	@RequestMapping("/module/sync2/verifyJson")
	public String verifyJson(@RequestParam("json") String json) throws SyncException {
		// TODO redirect to verifyJson method in LoadSyncConfigPageController class
		return "/sync2/verifyJson.htm?" + json; //this is wrong
		// or duplicate sync2.js and add "/module" before "verifyJson.htm"
		// and then do all verifying logic here
	}

	// TODO
	@RequestMapping(value = "/module/sync2/importSyncConfiguration", method = RequestMethod.POST)
	public String saveConfiguration(@RequestParam(value = "file") MultipartFile file,
			ModelMap model) throws SyncException, IOException {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			IOUtils.copy(file.getInputStream(), writer, "UTF-8");
			SyncConfiguration syncConfiguration = parseJsonStringToSyncConfiguration(writer.toString());
			syncConfigurationService.saveConfiguration(syncConfiguration);
			model.addAttribute("importStatus", "");
			return "redirect:/sync2/sync2.page";
		} catch (SyncException e) {
			LOGGER.warn("Error during import configuration:", e);
			model.addAttribute("importStatus", messageSourceService.getMessage("sync2.configuration.errors.invalidFile"));
		} finally {
			IOUtils.closeQuietly(writer);
		}
		return "/module/sync2/sync2Configuration";
	}
}
