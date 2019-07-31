package org.openmrs.module.sync2.api19.helper;

import org.openmrs.activelist.Allergy;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.sync2.api.helper.AllergyHelper;
import org.springframework.stereotype.Component;

@Component(value = "sync2.AllergyHelper1_9")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.* - 1.12.*")
public class AllergyHelperImpl1_9 implements AllergyHelper {

	@Override
	public Class getAllergyClass() {
		return Allergy.class;
	}
}
