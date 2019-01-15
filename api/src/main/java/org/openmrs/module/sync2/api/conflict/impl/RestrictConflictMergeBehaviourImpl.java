package org.openmrs.module.sync2.api.conflict.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.fhir.api.merge.MergeBehaviour;
import org.openmrs.module.fhir.api.merge.MergeConflict;
import org.openmrs.module.fhir.api.merge.MergeResult;
import org.openmrs.module.fhir.api.merge.MergeSuccess;
import org.openmrs.module.sync2.api.model.ParentObjectHashcode;
import org.openmrs.module.sync2.api.model.SyncObject;
import org.openmrs.module.sync2.api.service.ParentObjectHashcodeService;
import org.openmrs.module.sync2.api.utils.SyncHashcodeUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sync2.restrictConflictMergeBehaviour")
public class RestrictConflictMergeBehaviourImpl implements MergeBehaviour<SyncObject> {

	private static final String UUID_KEY = "uuid";

	@Autowired
	private ParentObjectHashcodeService parentObjectHashcodeService;

	@Override
	public MergeResult<SyncObject> resolveDiff(Class<? extends SyncObject> clazz, SyncObject local, SyncObject foreign) {
		return revolveConflict(local, foreign);
	}

	private MergeResult<SyncObject> revolveConflict(SyncObject source, SyncObject target) {
		Class storedClass = SimpleObject.class;
		MergeResult result = new MergeConflict<>(storedClass, source.getSimpleObject(), target.getSimpleObject());
		String sourceHashCode = SyncHashcodeUtils.getHashcode(source.getSimpleObject());
		String targetHashCode = SyncHashcodeUtils.getHashcode(target.getSimpleObject());
		String objectUuid = (source.getSimpleObject() != null ) ? (String) source.getSimpleObject().get(UUID_KEY) : null;
		ParentObjectHashcode previousHashCode = parentObjectHashcodeService.getByObjectUuid(objectUuid);

		if (StringUtils.isNotBlank(sourceHashCode) && StringUtils.isNotBlank(targetHashCode)) {
			if (previousHashCode == null) {
				result = new MergeSuccess<>(storedClass, source.getSimpleObject(), target.getSimpleObject(),
						source.getBaseObject(), true, false);
			} else {
				boolean sourceEquals = previousHashCode.getHashcode().equalsIgnoreCase(sourceHashCode);
				boolean targetEquals = previousHashCode.getHashcode().equalsIgnoreCase(targetHashCode);
				if (sourceEquals && !targetEquals) {
					result = new MergeSuccess<>(storedClass, source.getSimpleObject(), target.getSimpleObject(),
							target.getBaseObject(), false, true);
				} else if (!sourceEquals && targetEquals) {
					result = new MergeSuccess<>(storedClass, source.getSimpleObject(), target.getSimpleObject(),
							source.getBaseObject(), true, false);
				}
			}
		}
		return result;
	}
}
