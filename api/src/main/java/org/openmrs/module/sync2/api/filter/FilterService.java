package org.openmrs.module.sync2.api.filter;

import org.openmrs.module.sync2.api.model.SyncCategory;

public interface FilterService {

    boolean shouldBeSynced(SyncCategory category, Object object, String action);
}
