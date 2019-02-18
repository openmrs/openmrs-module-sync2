package org.openmrs.module.sync2.api.utils;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.helper.AllergyHelper;
import org.openmrs.module.sync2.api.helper.CategoryHelper;
import org.openmrs.module.sync2.api.service.EventConfigurationService;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;

import java.util.List;

public class ContextUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

	public static <T> T getRegisteredComponentSafely(String beanName, Class<T> clazz) {
		try {
			return Context.getRegisteredComponent(beanName, clazz);
		} catch (APIException ex) {
			LOGGER.debug("Could not fetch '{}' component", ex);
			return null;
		}
	}

	public static <T> T getFirstRegisteredComponent(Class<T> clazz) {
		List<T> list = Context.getRegisteredComponents(clazz);
		if (list.isEmpty()) {
			throw new SyncException(String.format("Not found any instances of '%s' component in the context",
					clazz.getName()));
		}
		return Context.getRegisteredComponents(clazz).get(SyncConstants.ZERO);
	}

	public static ConversionService getConversionService() {
		return Context.getRegisteredComponent("conversionService", ConversionService.class);
	}

	public static EventConfigurationService getEventConfigurationService() {
		String eventHandlerName = SyncUtils.getEventHandlerName();
		return Context.getRegisteredComponent("sync2.eventConfigurationService." + eventHandlerName,
				EventConfigurationService.class);
	}

	public static LocalFeedReader getLocalFeedReader() {
		String eventHandlerName = SyncUtils.getEventHandlerName();
		return Context.getRegisteredComponent("sync2.localFeedReader." + eventHandlerName, LocalFeedReader.class);
	}

	public static ParentFeedReader getParentFeedReader() {
		String eventHandlerName = SyncUtils.getEventHandlerName();
		return Context.getRegisteredComponent("sync2.parentFeedReader." + eventHandlerName, ParentFeedReader.class);
	}

	public static SyncPushService getSyncPushService() {
		return Context.getRegisteredComponent(SyncConstants.SYNC_PUSH_SERVICE_BEAN, SyncPushService.class);
	}

	public static SyncPullService getSyncPullService() {
		return Context.getRegisteredComponent(SyncConstants.SYNC_PULL_SERVICE_BEAN, SyncPullService.class);
	}

	public static CategoryHelper getCategoryHelper() {
		return Context.getRegisteredComponent(SyncConstants.SYNC_CATEGORY_HELPER, CategoryHelper.class);
	}

	public static AllergyHelper getAllergyHelper() {
		return getFirstRegisteredComponent(AllergyHelper.class);
	}

	private ContextUtils() {}
}
