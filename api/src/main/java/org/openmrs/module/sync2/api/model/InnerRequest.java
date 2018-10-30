package org.openmrs.module.sync2.api.model;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

public class InnerRequest implements Serializable {

	protected HttpMethod method;

	protected URI url;

	protected String body;

	public InnerRequest() {
	}

	public InnerRequest(RequestEntity<?> entity) {
		this.method = entity.getMethod();
		this.url = entity.getUrl();
		this.body = (entity.getBody() != null) ? entity.getBody().toString() : null;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public URI getUrl() {
		return url;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof InnerRequest))
			return false;
		InnerRequest that = (InnerRequest) o;
		return method == that.method &&
				Objects.equals(url, that.url) &&
				Objects.equals(body, that.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(method, url, body);
	}

	@Override
	public String toString() {
		return "{" +
				"\"method\":\"" + method + '\"' +
				", \"url\":\"" + url + '\"' +
				", \"body\":\"" + body + '\"' +
				'}';
	}
}
