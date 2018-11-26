package org.openmrs.module.sync2.api.conflict.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.fhir.api.merge.MergeBehaviour;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component("sync2.newIsTheBestMergeBehaviourImpl")
public class NewIsTheBestMergeBehaviourImpl implements MergeBehaviour<SyncObject> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewIsTheBestMergeBehaviourImpl.class);

	private static final String AUDIT_INFO_KEY = "auditInfo";

	private static final String DATE_CHANGED_KEY = "dateChanged";

	@Override
	public MergeResult<SyncObject> resolveDiff(Class<? extends SyncObject> clazz, SyncObject local, SyncObject foreign) {
		return resolveConflict(foreign, local);
	}

	private MergeResult<SyncObject> resolveConflict(SyncObject foreign, SyncObject local) {
		Class storedClass = SimpleObject.class;
		MergeResult<SyncObject> result = new MergeConflict<>(storedClass, foreign.getSimpleObject(), local.getSimpleObject());
		Map<String, Object> localAuditInfo = foreign.getSimpleObject().get(AUDIT_INFO_KEY);
		String localDateChangedStr = localAuditInfo != null ? (String) localAuditInfo.get(DATE_CHANGED_KEY) : null;

		Map<String, Object> foreignAuditInfo = local.getSimpleObject().get(AUDIT_INFO_KEY);
		String foreignDateChangedStr = foreignAuditInfo != null ? (String) foreignAuditInfo.get(DATE_CHANGED_KEY) : null;

		if (StringUtils.isBlank(localDateChangedStr)) {
			result = new MergeSuccess<>(storedClass, foreign.getSimpleObject(), local.getSimpleObject(),
					local.getBaseObject(), false, true);
		} else if (StringUtils.isNotBlank(localDateChangedStr) && StringUtils.isNotBlank(foreignDateChangedStr)) {
			DateFormat formatter = new SimpleDateFormat(ConversionUtil.DATE_FORMAT);
			try {
				Date localDateChanged = formatter.parse(localDateChangedStr);
				Date foreignDateChanged = formatter.parse(foreignDateChangedStr);
				if (localDateChanged.before(foreignDateChanged)) {
					result = new MergeSuccess<>(storedClass, foreign.getSimpleObject(), local.getSimpleObject(),
							foreign.getBaseObject(), false, true);
				} else {
					result = new MergeSuccess<>(storedClass, foreign.getSimpleObject(), local.getSimpleObject(),
							local.getBaseObject(), true, false);
				}
			}
			catch (ParseException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}
}
