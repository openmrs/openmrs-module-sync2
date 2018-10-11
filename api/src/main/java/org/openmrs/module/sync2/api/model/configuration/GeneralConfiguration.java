package org.openmrs.module.sync2.api.model.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneralConfiguration {

	private String parentFeedLocation;

	private String localFeedLocation;

	private String localInstanceId;

	private boolean persistSuccessAudit;

	private boolean persistFailureAudit;

	private List<String> filterBeans;

	public GeneralConfiguration() {
		this.filterBeans = new ArrayList<>();
	}

	public GeneralConfiguration(String parentFeedLocation, String localFeedLocation, String localInstanceId,
			boolean persistSuccessAudit, boolean persistFailureAudit, List<String> filterBeans) {
		this.parentFeedLocation = parentFeedLocation;
		this.localFeedLocation = localFeedLocation;
		this.localInstanceId = localInstanceId;
		this.persistSuccessAudit = persistSuccessAudit;
		this.persistFailureAudit = persistFailureAudit;
		this.filterBeans = filterBeans;
	}

	public String getParentFeedLocation() {
		return parentFeedLocation;
	}

	public void setParentFeedLocation(String parentFeedLocation) {
		this.parentFeedLocation = parentFeedLocation;
	}

	public String getLocalFeedLocation() {
		return localFeedLocation;
	}

	public void setLocalFeedLocation(String localFeedLocation) {
		this.localFeedLocation = localFeedLocation;
	}

	public boolean isPersistSuccessAudit() {
		return persistSuccessAudit;
	}

	public void setPersistSuccessAudit(boolean persistSuccessAudit) {
		this.persistSuccessAudit = persistSuccessAudit;
	}

	public boolean isPersistFailureAudit() {
		return persistFailureAudit;
	}

	public void setPersistFailureAudit(boolean persistFailureAudit) {
		this.persistFailureAudit = persistFailureAudit;
	}

	public String getLocalInstanceId() {
		return localInstanceId;
	}

	public void setLocalInstanceId(String localInstanceId) {
		this.localInstanceId = localInstanceId;
	}

	public List<String> getFilterBeans() {
		return filterBeans;
	}

	public void setFilterBeans(List<String> filterBeans) {
		this.filterBeans = filterBeans;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GeneralConfiguration that = (GeneralConfiguration) o;
		return Objects.equals(parentFeedLocation, that.parentFeedLocation)
				&& Objects.equals(localFeedLocation, that.localFeedLocation)
				&& Objects.equals(localInstanceId, that.localInstanceId)
				&& Objects.equals(persistSuccessAudit, that.persistSuccessAudit)
				&& Objects.equals(persistFailureAudit, that.persistFailureAudit)
				&& Objects.equals(filterBeans, that.filterBeans);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parentFeedLocation, localFeedLocation, localInstanceId,
				persistSuccessAudit, persistFailureAudit, filterBeans);
	}
}
