package org.openmrs.module.sync2.api.model.configuration;

import java.io.Serializable;
import java.util.Objects;

public class ClientConfiguration implements Serializable {

	private static final long serialVersionUID = -2597794749882756894L;

	private String hostAddress;

	public ClientConfiguration() { }

	public ClientConfiguration(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClientConfiguration that = (ClientConfiguration) o;
		return Objects.equals(hostAddress, that.hostAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hostAddress);
	}
}
