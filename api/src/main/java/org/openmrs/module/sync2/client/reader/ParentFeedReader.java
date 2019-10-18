package org.openmrs.module.sync2.client.reader;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.SyncCategory;

/**
 * <h1>ParentFeedReader</h1>
 * Handles event processing as a parent instance.
 *
 */
public interface ParentFeedReader {

	/**
	 * <p>Reads all events and processes the data for every supported category.</p>
	 *
	 */
	void pullAndProcessAllFeeds();

	/**
	 * <p>Reads all events and processes the data for specific, supported category.</p>
	 *
	 * @param category represents a type of object which is supported by synchronization.
	 * @throws SyncException when event processing has failed.
	 */
	void pullAndProcessFeeds(SyncCategory category) throws SyncException;
	
}
