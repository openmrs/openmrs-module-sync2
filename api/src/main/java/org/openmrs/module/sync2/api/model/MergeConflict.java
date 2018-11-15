package org.openmrs.module.sync2.api.model;

import org.openmrs.BaseOpenmrsData;

import java.util.Arrays;
import java.util.Objects;

public class MergeConflict extends BaseOpenmrsData {

	private static final long serialVersionUID = 2106212376155338054L;

	private Integer id;

	private String fullClassName;

	private byte[] orgLocal;

	private byte[] orgForeign;

	private String message;

	public MergeConflict() {}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}

	public byte[] getOrgLocal() {
		return orgLocal;
	}

	public void setOrgLocal(byte[] orgLocal) {
		this.orgLocal = orgLocal;
	}

	public byte[] getOrgForeign() {
		return orgForeign;
	}

	public void setOrgForeign(byte[] orgForeign) {
		this.orgForeign = orgForeign;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		MergeConflict that = (MergeConflict) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(fullClassName, that.fullClassName) &&
				Arrays.equals(orgLocal, that.orgLocal) &&
				Arrays.equals(orgForeign, that.orgForeign) &&
				Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(super.hashCode(), id, fullClassName, message);
		result = 31 * result + Arrays.hashCode(orgLocal);
		result = 31 * result + Arrays.hashCode(orgForeign);
		return result;
	}
}
