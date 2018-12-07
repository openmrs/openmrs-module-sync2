package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.webservices.rest.SimpleObject;

import javax.transaction.NotSupportedException;

public interface UnifyService {

	SimpleObject unifyObject(Object object, CategoryEnum category, String clientName) throws NotSupportedException;
}
