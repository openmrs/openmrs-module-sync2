package org.openmrs.module.sync2.api.conflict.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.fhir.api.merge.MergeBehaviour;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("sync2.newIsTheBestMergeBehaviourImpl")
public class NewIsTheBestMergeBehaviourImpl implements MergeBehaviour<SimpleObject> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewIsTheBestMergeBehaviourImpl.class);

	private static final String AUDIT_INFO_KEY = "auditInfo";

	private static final String DATE_CHANGED_KEY = "dateChanged";

	@Override
	public MergeResult<SimpleObject> resolveDiff(Class<? extends SimpleObject> clazz, SimpleObject local,
			SimpleObject foreign) {
		return resolveConflict(clazz, local, foreign);
	}

	private MergeResult resolveConflict(Class<? extends SimpleObject> clazz, SimpleObject local, SimpleObject foreign) {
		MergeResult result = new MergeConflict(clazz, local, foreign);

		SimpleObject localAuditInfo = local.get(AUDIT_INFO_KEY);
		String localDateChangedStr = localAuditInfo != null ? localAuditInfo.get(DATE_CHANGED_KEY) : null;

		SimpleObject foreignAuditInfo = foreign.get(AUDIT_INFO_KEY);
		String foreignDateChangedStr = foreignAuditInfo != null ? foreignAuditInfo.get(DATE_CHANGED_KEY) : null;

		if (StringUtils.isBlank(localDateChangedStr)) {
			result = new MergeSuccess(clazz, local, foreign, foreign, false, true);
		} else if (StringUtils.isNotBlank(localDateChangedStr) && StringUtils.isNotBlank(foreignDateChangedStr)) {
			DateFormat formatter = new SimpleDateFormat(ConversionUtil.DATE_FORMAT);
			try {
				Date localDateChanged = formatter.parse(localDateChangedStr);
				Date foreignDateChanged = formatter.parse(foreignDateChangedStr);
				if (localDateChanged.before(foreignDateChanged)) {
					result = new MergeSuccess(clazz, local, foreign, foreign, false, true);
				} else {
					result = new MergeSuccess(clazz, local, foreign, local, true, false);
				}
			}
			catch (ParseException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}
}
