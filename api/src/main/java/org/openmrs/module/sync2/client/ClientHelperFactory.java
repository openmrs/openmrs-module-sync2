package org.openmrs.module.sync2.client;

import org.openmrs.module.fhir.api.helper.ClientHelper;
import org.openmrs.module.fhir.api.helper.FHIRClientHelper;
import org.openmrs.module.sync2.client.rest.RESTClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openmrs.module.sync2.SyncConstants.FHIR_CLIENT;
import static org.openmrs.module.sync2.SyncConstants.REST_CLIENT;

public class ClientHelperFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientHelperFactory.class);

	public ClientHelper createClient(final String clientType) {
		switch (clientType) {
			case REST_CLIENT:
				return new RESTClientHelper();
			case FHIR_CLIENT:
				return new FHIRClientHelper();
			default:
				LOGGER.warn(String.format("Unrecognized clientType: %s", clientType));
				return null;
		}
	}
}
