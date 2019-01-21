package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.fhir.api.helper.FHIRClientHelper;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;
import org.openmrs.module.sync2.api.service.UnifyService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.springframework.stereotype.Service;

import javax.transaction.NotSupportedException;

@Service("sync2.unifyService")
public class UnifyServiceImpl implements UnifyService {

	@Override
	public SimpleObject unifyObject(Object object, SyncCategory category, String clientName) throws NotSupportedException {
		Object result = null;

		if (isObjectAlreadyUnified(object)) {
			result = object;
		} else if (object instanceof String) {
			result = new SimpleObject().add("uuid", object);
		} else if (object != null) {

			if (SyncConstants.FHIR_CLIENT.equals(clientName)) {
				FHIRClientHelper helper = new FHIRClientHelper();
				result = helper.convertToOpenMrsObject(object, category.getCategory());
				try {
					result = ConversionUtil.convertToRepresentation(result, Representation.FULL);
				} catch (ConversionException e) {
					throw new SyncException("Conversion error occurred. Check if date format (" + ConversionUtil.DATE_FORMAT +
							") is corrected.", e);
				}
			} else if (AuditMessage.class.isAssignableFrom(category.getClazz())) {
				result = ConversionUtil.convertToRepresentation(object, Representation.FULL);
			} else {
				throw new NotSupportedException(String.format("Category %s not supported.", category.getCategory()));
			}
		}

		return (SimpleObject) result;
	}

	private boolean isObjectAlreadyUnified(Object object) {
		return object instanceof SimpleObject;
	}
}
