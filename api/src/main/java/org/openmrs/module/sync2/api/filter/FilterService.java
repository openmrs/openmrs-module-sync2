package org.openmrs.module.sync2.api.filter;

public interface FilterService {

    boolean shouldBeSynced(String category, Object object, String action);
}
