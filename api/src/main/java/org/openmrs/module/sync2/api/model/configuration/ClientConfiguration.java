package org.openmrs.module.sync2.api.model.configuration;

import java.io.Serializable;
import java.util.Objects;

public class ClientConfiguration implements Serializable {

	private static final long serialVersionUID = -2597794749882756894L;

	private String hostAddress;

	private String login;

	private String password;

	public ClientConfiguration() { }

	public ClientConfiguration(String hostAddress, String login, String password) {
		this.hostAddress = hostAddress;
		this.login = login;
		this.password = password;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClientConfiguration that = (ClientConfiguration) o;
		return Objects.equals(hostAddress, that.hostAddress) &&
				Objects.equals(login, that.login) &&
				Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hostAddress, login, password);
	}
}
