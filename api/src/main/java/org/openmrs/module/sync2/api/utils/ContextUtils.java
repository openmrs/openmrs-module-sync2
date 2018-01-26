package org.openmrs.module.sync2.api.utils;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

    public static <T> T getRegisteredComponent(String beanName, Class<T> clazz) {
        try {
            return Context.getRegisteredComponent(beanName, clazz);
        } catch (APIException ex) {
            LOGGER.debug("Could not fetch '{}' component", ex);
            return null;
        }
    }

    private ContextUtils() {}
}
