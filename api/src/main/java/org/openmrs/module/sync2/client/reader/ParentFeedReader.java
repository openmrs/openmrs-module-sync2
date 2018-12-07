package org.openmrs.module.sync2.client.reader;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

public interface ParentFeedReader {

	void pullAndProcessAllFeeds();

	void pullAndProcessFeeds(CategoryEnum category) throws SyncException;
}
