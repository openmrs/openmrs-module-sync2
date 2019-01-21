package org.openmrs.module.sync2.api.helper;

import org.openmrs.module.sync2.api.model.SyncCategory;

public interface CategoryHelper {

	SyncCategory getByCategory(String category);
}
