package org.openmrs.module.sync2.page.controller;

import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class AuditListPageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditListPageController.class);

    public void controller(PageModel model) {
        model.addAttribute("localInstanceId", SyncUtils.getLocalInstanceId());
    }
}
