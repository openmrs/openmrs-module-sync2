package org.openmrs.module.sync2.api20.helper;

import org.openmrs.Allergy;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.sync2.api.helper.AllergyHelper;
import org.springframework.stereotype.Component;

@Component(value = "sync2.AllergyHelper2_0")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.*")
public class AllergyHelperImpl2_0 implements AllergyHelper {

	@Override
	public Class getAllergyClass() {
		return Allergy.class;
	}
}
