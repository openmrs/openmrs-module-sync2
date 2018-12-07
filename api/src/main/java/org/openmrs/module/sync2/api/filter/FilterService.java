package org.openmrs.module.sync2.api.filter;

import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

public interface FilterService {

    boolean shouldBeSynced(CategoryEnum category, Object object, String action);
}
