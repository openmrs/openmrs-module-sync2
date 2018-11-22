package org.openmrs.module.sync2.api.service;

import javax.transaction.NotSupportedException;

public interface UnifyService {

	Object unifyObject(Object object, String category, String clientName) throws NotSupportedException;
}
