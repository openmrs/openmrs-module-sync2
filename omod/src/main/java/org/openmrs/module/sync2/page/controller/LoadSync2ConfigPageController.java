package org.openmrs.module.sync2.page.controller;

import org.apache.commons.io.IOUtils;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.sync2.api.Sync2ConfigurationService;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.openmrs.module.sync2.api.model.configuration.Sync2Configuration;
import org.openmrs.module.sync2.api.utils.Sync2Utils;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
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

@Controller
public class LoadSync2ConfigPageController {

    private static final String SAVE_CONFIG_ERROR = "sync2.configuration.json.save.fail";
    private static final String SAVE_CONFIG_SUCCESS = "sync2.configuration.json.save.success";
    private static final String VIEW_PROVIDER = "loadSync2Config";

    @Autowired
    Sync2ConfigurationService sync2ConfigurationService;

    @Autowired
    @Qualifier("messageSourceService")
    private MessageSourceService messageSourceService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(LoadSync2ConfigPageController.class);

    public String get(PageModel model,
                      @RequestParam(value = "importStatus", required = false) String importStatus,
                      @SpringBean("sync2.sync2ConfigurationService") Sync2ConfigurationService sync2ConfigurationService) {
        String configuration =
                Sync2Utils.writeSyncConfigurationToJsonString(sync2ConfigurationService.getSync2Configuration());
        model.addAttribute("configuration", configuration);
        model.addAttribute("importStatus", importStatus);
        return VIEW_PROVIDER;
    }

    public String post(PageModel model,
                       @SpringBean("sync2.sync2ConfigurationService") Sync2ConfigurationService sync2ConfigurationService,
                       @RequestParam("json") String json, HttpSession session, UiUtils ui) {
        try {
            sync2ConfigurationService.saveConfiguration(json);
            InfoErrorMessageUtil.flashInfoMessage(session, ui.message(SAVE_CONFIG_SUCCESS));

            return "redirect:/sync2/sync2.page";
        } catch (Exception e) {
            LOGGER.warn("Error during save:", e);

            session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui.message(SAVE_CONFIG_ERROR));
        }

        return "redirect:/sync2/loadSync2Config.page";
    }

    @ResponseBody
    @RequestMapping("/sync2/verifyJson")
    public SimpleObject verifyJson(@RequestParam("json") String json) throws  Sync2Exception {
        SimpleObject result = new SimpleObject();
        if (Sync2Utils.isValidateJson(json)) {
            result.put("isValid", true);
        } else  {
            LOGGER.warn("Invalid json.");
            result.put("isValid", false);
        }

        return result;
    }

    @RequestMapping(value = "/sync2/importSyncConfiguration", method = RequestMethod.POST)
    public String importSyncConfiguration(@RequestParam("file") MultipartFile file,
                                          ModelMap model) throws Sync2Exception, IOException {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            IOUtils.copy(file.getInputStream(), writer, "UTF-8");

            Sync2Configuration sync2Configuration = Sync2Utils.parseJsonStringToSyncConfiguration(writer.toString());
            sync2ConfigurationService.saveConfiguration(sync2Configuration);
            model.addAttribute("importStatus", "");
            return "redirect:/sync2/sync2.page";
        } catch (Sync2Exception e) {
            LOGGER.warn("Error during import configuration:", e);
            model.addAttribute("importStatus", messageSourceService.getMessage("sync2.configuration.errors.invalidFile"));
        } finally {
            IOUtils.closeQuietly(writer);
        }

        return "redirect:/sync2/loadSync2Config.page";
    }
}
