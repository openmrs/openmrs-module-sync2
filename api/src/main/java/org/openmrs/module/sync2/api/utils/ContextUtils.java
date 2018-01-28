package org.openmrs.module.sync2.api.utils;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;

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

    public static ConversionService getConversionService() {
        return Context.getRegisteredComponent("conversionService", ConversionService.class);
    }

    private ContextUtils() {}
}
