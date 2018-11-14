package org.openmrs.module.sync2.api.dao;

import org.openmrs.module.sync2.api.model.ParentObjectHashcode;

public interface ParentObjectHashcodeDao {

	ParentObjectHashcode getById(Integer id);

	ParentObjectHashcode getByUuid(String uuid);

	ParentObjectHashcode getByObjectUuid(String objectUuid);

	ParentObjectHashcode save(ParentObjectHashcode parentObjectHashcode);
}
