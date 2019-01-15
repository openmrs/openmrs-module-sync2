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

@Component("sync2.newIsTheBestMergeBehaviour")
public class NewIsTheBestMergeBehaviourImpl implements MergeBehaviour<SyncObject> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewIsTheBestMergeBehaviourImpl.class);

	private static final String AUDIT_INFO_KEY = "auditInfo";

	private static final String DATE_CHANGED_KEY = "dateChanged";

	@Override
	public MergeResult<SyncObject> resolveDiff(Class<? extends SyncObject> clazz, SyncObject local, SyncObject foreign) {
		return resolveConflict(local, foreign);
	}

	private MergeResult<SyncObject> resolveConflict(SyncObject source, SyncObject target) {
		Class storedClass = SimpleObject.class;
		MergeResult result = new MergeConflict<>(storedClass, source.getSimpleObject(), target.getSimpleObject());
		Map<String, Object> sourceAuditInfo = source.getSimpleObject().get(AUDIT_INFO_KEY);
		String sourceDateChangedStr = sourceAuditInfo != null ? (String) sourceAuditInfo.get(DATE_CHANGED_KEY) : null;

		Map<String, Object> targerAuditInfo = target.getSimpleObject().get(AUDIT_INFO_KEY);
		String targetDateChangedStr = targerAuditInfo != null ? (String) targerAuditInfo.get(DATE_CHANGED_KEY) : null;

		if (StringUtils.isBlank(targetDateChangedStr)) {
			result = new MergeSuccess<>(storedClass, source.getSimpleObject(), target.getSimpleObject(),
					source.getBaseObject(), true, false);
		} else if (StringUtils.isNotBlank(sourceDateChangedStr) && StringUtils.isNotBlank(targetDateChangedStr)) {
			DateFormat formatter = new SimpleDateFormat(ConversionUtil.DATE_FORMAT);
			try {
				Date sourceDateChanged = formatter.parse(sourceDateChangedStr);
				Date targetDateChanged = formatter.parse(targetDateChangedStr);
				if (sourceDateChanged.before(targetDateChanged)) {
					result = new MergeSuccess<>(storedClass, source.getSimpleObject(), target.getSimpleObject(),
							target.getBaseObject(), false, true);
				} else {
					result = new MergeSuccess<>(storedClass, source.getSimpleObject(), target.getSimpleObject(),
							source.getBaseObject(), true, false);
				}
			}
			catch (ParseException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}
}
