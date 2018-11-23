package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.fhir.api.helper.FHIRClientHelper;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.service.UnifyService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.stereotype.Service;

import javax.transaction.NotSupportedException;

@Service("sync2.unifyService")
public class UnifyServiceImpl implements UnifyService {

	@Override
	public Object unifyObject(Object object, String category, String clientName) throws NotSupportedException {
		Object result = null;

		if (isObjectAlreadyUnified(object)) {
			result = object;
		} else {
			CategoryEnum cat = CategoryEnum.getByCategory(category);

			if (SyncConstants.FHIR_CLIENT.equals(clientName)) {
				FHIRClientHelper helper = new FHIRClientHelper();
				result = helper.convertToOpenMrsObject(object, category);
				result = ConversionUtil.convertToRepresentation(result, Representation.FULL);
			} else if (AuditMessage.class.isAssignableFrom(cat.getClazz())) {
				result = ConversionUtil.convertToRepresentation(object, Representation.FULL);
			} else {
				throw new NotSupportedException(String.format("Category %s not supported.", category));
			}
		}

		return result;
	}

	private boolean isObjectAlreadyUnified(Object object) {
		return object instanceof SimpleObject;
	}
}
