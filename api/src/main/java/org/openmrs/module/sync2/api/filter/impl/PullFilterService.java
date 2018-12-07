package org.openmrs.module.sync2.api.filter.impl;

import org.openmrs.module.sync2.api.filter.FilterConstant;
import org.openmrs.module.sync2.api.filter.FilterService;
import org.openmrs.module.sync2.api.filter.ObjectFilter;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.springframework.stereotype.Component;

@Component
public class PullFilterService implements FilterService {

    private static final boolean DEFAULT_RESULT = true;

    @Override
    public boolean shouldBeSynced(CategoryEnum category, Object object, String action) {
        ObjectFilter objectFilter = getFilter(category.getCategory());
        if (objectFilter != null) {
            return objectFilter.shouldObjectBeSynced(object, action);
        } else {
            return DEFAULT_RESULT;
        }
    }

    private ObjectFilter getFilter(String category) {
        return ContextUtils.getRegisteredComponentSafely(
                FilterConstant.PULL_FILTERS_COMPONENT_PREFIX + category,
                ObjectFilter.class);
    }
}
