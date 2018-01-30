package org.openmrs.module.sync2.api.filter;

public interface ObjectFilter {

    boolean shouldObjectBeSynced(Object object, String action);
}
