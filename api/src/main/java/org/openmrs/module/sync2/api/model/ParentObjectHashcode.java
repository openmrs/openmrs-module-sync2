package org.openmrs.module.sync2.api.model;

import org.openmrs.BaseOpenmrsData;

import java.util.Objects;

public class ParentObjectHashcode extends BaseOpenmrsData {

	private Integer id;

	private String objectUuid;

	private String hashcode;

	public ParentObjectHashcode() {}

	public ParentObjectHashcode(String objectUuid, String hashcode) {
		this.objectUuid = objectUuid;
		this.hashcode = hashcode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getObjectUuid() {
		return objectUuid;
	}

	public void setObjectUuid(String objectUuid) {
		this.objectUuid = objectUuid;
	}

	public String getHashcode() {
		return hashcode;
	}

	public void setHashcode(String hashcode) {
		this.hashcode = hashcode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		ParentObjectHashcode that = (ParentObjectHashcode) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(objectUuid, that.objectUuid) &&
				Objects.equals(hashcode, that.hashcode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, objectUuid, hashcode);
	}
}
