package org.openmrs.module.sync2.client.reader;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

/**
 * <h1>LocalFeedReader</h1>
 * Handles event processing as a child instance.
 *
 */
public interface LocalFeedReader {

	/**
	 * <p>Reads all events and pushes the data for every supported category.</p>
	 *
	 */
	void readAndPushAllFeeds();

	/**
	 * <p>Reads all events and pushes the data for specific, supported category.</p>
	 *
	 * @param category represents a type of object which is supported by synchronization.
	 * @throws SyncException when event processing has failed.
	 */
	void readAndPushAllFeeds(CategoryEnum category) throws SyncException;
}
