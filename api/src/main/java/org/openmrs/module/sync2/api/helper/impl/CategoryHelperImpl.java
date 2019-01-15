package org.openmrs.module.sync2.api.helper.impl;

import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.sync2.api.helper.CategoryHelper;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.springframework.stereotype.Component;

@Component(value = "sync2.categoryHelper")
public class CategoryHelperImpl implements CategoryHelper {

	@Override
	public SyncCategory getByCategory(String category) {
		SyncCategory syncCategory = null;
		if (category.equalsIgnoreCase(FHIRConstants.CATEGORY_ALLERGY)) {
			syncCategory = buildSyncCategoryObject(category, ContextUtils.getAllergyHelper().getAllergyClass());
		} else {
			syncCategory = buildSyncCategoryObject(CategoryEnum.getByCategory(category));
		}
		if (syncCategory == null) {
			throw new UnsupportedOperationException(String.format("Category %s is not supported", category));
		}
		return syncCategory;
	}

	private SyncCategory buildSyncCategoryObject(CategoryEnum categoryEnum) {
		if (categoryEnum == null) {
			return null;
		}
		return buildSyncCategoryObject(categoryEnum.getCategory(), categoryEnum.getClazz());
	}

	private SyncCategory buildSyncCategoryObject(String category, Class clazz) {
		return new SyncCategory(category, clazz);
	}
}
