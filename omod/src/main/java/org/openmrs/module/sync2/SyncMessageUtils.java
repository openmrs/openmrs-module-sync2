package org.openmrs.module.sync2;

import org.springframework.ui.ModelMap;

public class SyncMessageUtils {

	public static final String ALERT_MESSAGE_MODEL = "alertMessage";
	public static final String SUCCESS_MESSAGE = "success";

	public static void successMessage(ModelMap model, String message) {
		model.put(SUCCESS_MESSAGE, true);
		model.put(ALERT_MESSAGE_MODEL, message);
	}

	public static void errorMessage(ModelMap model, String message) {
		model.put(SUCCESS_MESSAGE, false);
		model.put(ALERT_MESSAGE_MODEL, message);
	}

	private SyncMessageUtils() { }

}
