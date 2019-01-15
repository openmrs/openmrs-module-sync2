package org.openmrs.module.sync2.page.controller;

import org.openmrs.annotation.OpenmrsProfile;
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
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class Sync2PageController {

	private static final String ATTRIBUTE_EMPTY_URI = "emptyURI";

	private static final String PARENT_URI_ERROR = "sync2.configuration.parentUrl.empty";

	/**
	 *
	 * Sets the UI model attribute used to check if the parent instance URI is valid.
	 * Notifies the user if mentioned URI is not valid.
	 *
	 * @param model injected the page model object
	 * @param session injected the HTTP session object
	 * @param ui injected the UiUtils object
	 */
	public void controller(PageModel model, HttpSession session, UiUtils ui) {
		boolean emptyURI = SyncUtils.parentInstanceUriIsEmpty();
		model.addAttribute(ATTRIBUTE_EMPTY_URI, emptyURI);
		if (emptyURI) {
			InfoErrorMessageUtil.flashErrorMessage(session, ui.message(PARENT_URI_ERROR));
		}
	}
}
