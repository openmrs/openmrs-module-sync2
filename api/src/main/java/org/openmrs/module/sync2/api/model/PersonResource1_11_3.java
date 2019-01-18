package org.openmrs.module.sync2.api.model;

import org.openmrs.Person;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;

@Resource(
		name = "v1/person",
		order = -1,
		supportedClass = Person.class,
		supportedOpenmrsVersions = {"1.11.3"}
)
public class PersonResource1_11_3 extends PersonResource1_8 {

	public PersonResource1_11_3() {
	}

	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (description != null) {
			description.addProperty("deathdateEstimated");
		}

		return description;
	}

	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addProperty("deathdateEstimated");
		return description;
	}

	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		description.addProperty("deathdateEstimated");
		return description;
	}

	public String getResourceVersion() {
		return "1.11.3";
	}

}
