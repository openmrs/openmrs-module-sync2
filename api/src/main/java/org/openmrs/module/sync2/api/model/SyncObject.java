package org.openmrs.module.sync2.api.model;

import org.openmrs.module.webservices.rest.SimpleObject;

import java.io.Serializable;
import java.util.Objects;

public class SyncObject implements Serializable {

	private static final long serialVersionUID = 1176269076155338012L;

	private Object baseObject;

	private SimpleObject simpleObject;

	public SyncObject() { }

	public SyncObject(Object baseObject) {
		this.baseObject = baseObject;
	}

	public SyncObject(Object baseObject, SimpleObject simpleObject) {
		this.baseObject = baseObject;
		this.simpleObject = simpleObject;
	}

	public Object getBaseObject() {
		return baseObject;
	}

	public void setBaseObject(Object baseObject) {
		this.baseObject = baseObject;
	}

	public SimpleObject getSimpleObject() {
		return simpleObject;
	}

	public void setSimpleObject(SimpleObject simpleObject) {
		this.simpleObject = simpleObject;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SyncObject that = (SyncObject) o;
		return Objects.equals(baseObject, that.baseObject) &&
				Objects.equals(simpleObject, that.simpleObject);
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseObject, simpleObject);
	}
}
