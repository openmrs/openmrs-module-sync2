package org.openmrs.module.sync2.client.reader;

import org.openmrs.module.sync2.api.exceptions.SyncException;

public interface LocalFeedReader {

    void readAllFeedsForPush();

    void readFeedsForPush(String category) throws SyncException;
}
