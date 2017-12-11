package org.openmrs.module.sync2.page.controller;

import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
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
public class ManualSyncPageController  {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LoadSyncConfigPageController.class);

    private static final String SYNC_SUCCESS = "sync2.sync.success";

    public String controller(PageModel model,
                           @SpringBean("sync2.syncConfigurationService") SyncConfigurationService syncConfigurationService,
                           @SpringBean("sync2.parentFeedReader") ParentFeedReader parentFeedReader,
                           @SpringBean("sync2.localFeedReader") LocalFeedReader localFeedReader,
                           HttpSession session, UiUtils ui) {
        try {
            LOGGER.info("Start Parent Feed Reader...");
            try {
                parentFeedReader.readAllFeedsForPull();
            } catch (Exception e) {

            }

            localFeedReader.readAllFeedsForPush();


            InfoErrorMessageUtil.flashInfoMessage(session, ui.message(SYNC_SUCCESS));

            return "redirect:/sync2/sync2.page";

        } catch (Exception e) {
            throw new SyncException("Error during reading feeds: ", e);
        }
    }

}

