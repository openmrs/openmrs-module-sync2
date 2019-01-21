package org.openmrs.module.sync2.page.controller;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncValidationException;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

@Controller
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class ManualSyncPullPageController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSyncConfigPageController.class);
    
    private static final String SYNC_SUCCESS = "sync2.sync.pull.success";
    
    private static final String SYNC_FAILURE = "sync2.sync.pull.failure";
    
    public String controller(PageModel model,
                             @SpringBean("sync2.syncConfigurationService") SyncConfigurationService syncConfigurationService,
                             HttpSession session, UiUtils ui) {
        try {
            LOGGER.info("Start Parent Feed Reader...");
            ParentFeedReader parentFeedReader = ContextUtils.getParentFeedReader();
            parentFeedReader.pullAndProcessAllFeeds();
            InfoErrorMessageUtil.flashInfoMessage(session, ui.message(SYNC_SUCCESS));
        } catch (SyncValidationException e) {
            LOGGER.error("Error during reading feeds: ", e);
            InfoErrorMessageUtil.flashErrorMessage(session, ui.message(getFirstErrorCode(e.getErrors())));
        } catch (Exception e) {
            LOGGER.error("Error during reading feeds: ", e);
            InfoErrorMessageUtil.flashErrorMessage(session, ui.message(SYNC_FAILURE));
        }
        return "redirect:/sync2/sync2.page";
    }

    private String getFirstErrorCode(Errors errors) {
        return errors.getErrorsCodes().get(SyncConstants.ZERO);
    }
}

