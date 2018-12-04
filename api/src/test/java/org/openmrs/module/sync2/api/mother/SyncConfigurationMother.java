package org.openmrs.module.sync2.api.mother;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.ClientConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.model.configuration.WhitelistConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class SyncConfigurationMother {

	private static final String SAMPLE_LOCAL_INSTANCE_ID = "localInstanceId";

	private static final String CLIENT_SPECIFIC_ADDRESS = "http://address.org";

	private static final String CLIENT_LOGIN = "clientLogin";

	private static final String CLIENT_PASSWORD = "clientPassword";

	public static SyncConfiguration creteInstance(boolean withWhiteList, boolean clientsConf) {
		SyncConfiguration configuration = createInstance();

		if (withWhiteList) {
			List<String> instanceIds = new ArrayList<>();
			instanceIds.add("childInstanceId");
			WhitelistConfiguration whitelist = new WhitelistConfiguration(true, instanceIds);
			configuration.setWhitelist(whitelist);
		}
		if (clientsConf) {
			configuration.getGeneral().getClients().put(SyncConstants.REST_CLIENT,
					new ClientConfiguration(CLIENT_SPECIFIC_ADDRESS, CLIENT_LOGIN, CLIENT_PASSWORD));
		}

		return configuration;
	}

	private static SyncConfiguration createInstance() {
		SyncConfiguration configuration = new SyncConfiguration();
		GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress",
				SAMPLE_LOCAL_INSTANCE_ID, false, false, new LinkedHashMap<>());
		configuration.setGeneral(general);

		ClassConfiguration locationClass = new ClassConfiguration("Location",
				"location", "org.openmrs.Location", true, SyncConstants.FHIR_CLIENT);
		ClassConfiguration observationClass = new ClassConfiguration("Observation",
				"observation", "org.openmrs.Obs", true);
		List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

		SyncMethodConfiguration push = new SyncMethodConfiguration(true, 12, classes);
		configuration.setPush(push);

		SyncMethodConfiguration pull = new SyncMethodConfiguration(true, 12, classes);
		configuration.setPull(pull);
		return configuration;
	}

}
