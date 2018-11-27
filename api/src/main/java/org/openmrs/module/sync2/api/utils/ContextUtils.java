package org.openmrs.module.sync2.api.utils;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
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
            LOGGER.warn("Could not fetch '{}' component", ex);
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

    private ContextUtils() {}
}
