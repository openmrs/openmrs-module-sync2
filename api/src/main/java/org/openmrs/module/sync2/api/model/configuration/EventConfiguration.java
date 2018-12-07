package org.openmrs.module.sync2.api.model.configuration;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Objects;

public class EventConfiguration implements Serializable {

	private static final long serialVersionUID = 8269279054915628415L;

	private LinkedHashMap<String, String> linkTemplates;

	public EventConfiguration() { }

	public EventConfiguration(LinkedHashMap<String, String> linkTemplates) {
		this.linkTemplates = linkTemplates;
	}

	public LinkedHashMap<String, String> getLinkTemplates() {
		return linkTemplates;
	}

	public void setLinkTemplates(LinkedHashMap<String, String> linkTemplates) {
		this.linkTemplates = linkTemplates;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EventConfiguration that = (EventConfiguration) o;
		return Objects.equals(linkTemplates, that.linkTemplates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(linkTemplates);
	}
}
