package org.openmrs.module.sync2.client.reader;

import org.openmrs.module.sync2.api.exceptions.SyncException;

public interface LocalFeedReader {

	void readAndPushAllFeeds();

	void readAndPushAllFeeds(String category) throws SyncException;
}
