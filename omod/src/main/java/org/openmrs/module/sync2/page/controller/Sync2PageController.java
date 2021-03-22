package org.openmrs.module.sync2.page.controller;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
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
	
	private static final String ATTRIBUTE_PUSH_TOOGLE = "pushtoogle";
	
	private static final String PARENT_PUSH_ERROR = "sync2.globalProperty.parentpush.false";

	private static final String ATTRIBUTE_PULL_TOOGLE = "pulltoogle";

	private static final String PARENT_PULL_ERROR = "sync2.globalProperty.parentpull.false";

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
		
		boolean pushtoogle= Context.getAdministrationService().getGlobalPropertyValue("sync2.enableManualPushToParent", true);
		model.addAttribute(ATTRIBUTE_PUSH_TOOGLE, pushtoogle);
		if (pushtoogle == false) {
			InfoErrorMessageUtil.flashErrorMessage(session, ui.message(PARENT_PUSH_ERROR));
		}
		
		boolean pulltoogle= Context.getAdministrationService().getGlobalPropertyValue("sync2.enableManualPullFromParent", true);
		model.addAttribute(ATTRIBUTE_PULL_TOOGLE, pulltoogle);
		if (pulltoogle == false) {
			InfoErrorMessageUtil.flashErrorMessage(session, ui.message(PARENT_PULL_ERROR));
		}
	}

}
