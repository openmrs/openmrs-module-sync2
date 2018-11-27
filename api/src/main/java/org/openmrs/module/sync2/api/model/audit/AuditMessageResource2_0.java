package org.openmrs.module.sync2.api.model.audit;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;


/**
 * {@link Resource} for {@link AuditMessage}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/auditmessage", supportedClass = AuditMessage.class, supportedOpenmrsVersions = {
		"2.0.*", "2.1.*", "2.2.*" })
public class AuditMessageResource2_0 extends DataDelegatingCrudResource<AuditMessage> {

	/**
	 * @see org.openmrs.module.sync2.web.controller.rest.SyncAuditRestController
	 */
	@Override
	public AuditMessage getByUniqueId(String s) {
		throw new ResourceDoesNotSupportOperationException();
	}

	/**
	 * @see org.openmrs.module.sync2.web.controller.rest.SyncAuditRestController
	 */
	@Override
	protected void delete(AuditMessage auditMessage, String s, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	/**
	 * @see org.openmrs.module.sync2.web.controller.rest.SyncAuditRestController
	 */
	@Override
	public AuditMessage newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}

	/**
	 * @see org.openmrs.module.sync2.web.controller.rest.SyncAuditRestController
	 */
	@Override
	public AuditMessage save(AuditMessage auditMessage) {
		throw new ResourceDoesNotSupportOperationException();
	}

	/**
	 * @see org.openmrs.module.sync2.web.controller.rest.SyncAuditRestController
	 */
	@Override
	public void purge(AuditMessage auditMessage, RequestContext requestContext) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("voided");
			description.addProperty("success");
			description.addProperty("timestamp");
			description.addProperty("resourceName");
			description.addProperty("usedResourceUrl");
			description.addProperty("availableResourceUrls");
			description.addProperty("parentUrl");
			description.addProperty("localUrl");
			description.addProperty("details");
			description.addProperty("action");
			description.addProperty("operation");
			description.addProperty("linkType");
			description.addProperty("nextMessageUuid");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addProperty("success");
			description.addProperty("timestamp");
			description.addProperty("resourceName");
			description.addProperty("usedResourceUrl");
			description.addProperty("availableResourceUrls");
			description.addProperty("parentUrl");
			description.addProperty("localUrl");
			description.addProperty("details");
			description.addProperty("action");
			description.addProperty("operation");
			description.addProperty("linkType");
			description.addProperty("nextMessageUuid");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	/**
	 * @param auditMessage
	 * @return auditMessage's resourceName and date
	 */
	@PropertyGetter("display")
	public String getDisplayString(AuditMessage auditMessage) {
		String ret = auditMessage.getResourceName() == null ? "?" : auditMessage.getResourceName();
		ret += " ";
		ret += auditMessage.getTimestamp() == null ? "?" : Context.getDateFormat().format(
				auditMessage.getTimestamp());
		return ret;
	}
}
