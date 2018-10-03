package org.openmrs.module.sync2.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

/**
 * The Sync 2 page controller.
 */
@Controller
public class Sync2PageController {

	private static final String VALID_PARENT_INSTANCE_URI = "validParentInstanceUri";

	private static final String PARENT_URI_ERROR = "sync2.configuration.parentUrl.empty";

	/**
	 *
	 * Method using when page is refreshing.
	 *
	 * @param model injected the page model object
	 * @param session injected the HTTP session object
	 * @param ui injected the UiUtils object
	 */
	public void controller(PageModel model, HttpSession session, UiUtils ui) {
		boolean isValidParentInstanceUrl = parentInstanceUriIsEmpty();
		model.addAttribute(VALID_PARENT_INSTANCE_URI, isValidParentInstanceUrl);
		if (!isValidParentInstanceUrl) {
			InfoErrorMessageUtil.flashErrorMessage(session, ui.message(PARENT_URI_ERROR));
		}
	}

	private boolean parentInstanceUriIsEmpty() {
		return StringUtils.isNotBlank(SyncUtils.getParentBaseUrl());
	}
}
