package org.openmrs.module.sync2.page.controller;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.exceptions.SyncValidationException;
import org.openmrs.module.sync2.api.validator.Errors;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

@Controller
public class ManualSyncPushPageController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSyncConfigPageController.class);
    
    private static final String SYNC_SUCCESS = "sync2.sync.push.success";
    
    private static final String SYNC_FAILURE = "sync2.sync.push.failure";
    
    public String controller(PageModel model,
                             @SpringBean("sync2.syncConfigurationService") SyncConfigurationService syncConfigurationService,
                             @SpringBean("sync2.localFeedReader") LocalFeedReader localFeedReader,
                             HttpSession session, UiUtils ui) {
        try {
            LOGGER.info("Start Local Feed Reader...");
            localFeedReader.readAllFeedsForPush();
            InfoErrorMessageUtil.flashInfoMessage(session, ui.message(SYNC_SUCCESS));
        } catch (SyncValidationException e) {
            LOGGER.error("Error during pushing objects: ", e);
            InfoErrorMessageUtil.flashErrorMessage(session, ui.message(getFirstErrorCode(e.getErrors())));
        } catch (Exception e) {
            LOGGER.error("Error during pushing objects: ", e);
            InfoErrorMessageUtil.flashErrorMessage(session, ui.message(SYNC_FAILURE));
        }
        return "redirect:/sync2/sync2.page";
    }

    private String getFirstErrorCode(Errors errors) {
        return errors.getErrorsCodes().get(SyncConstants.ZERO);
    }
}

