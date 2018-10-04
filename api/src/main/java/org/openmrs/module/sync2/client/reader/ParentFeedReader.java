package org.openmrs.module.sync2.client.reader;

import org.openmrs.module.sync2.api.exceptions.SyncException;

public interface ParentFeedReader {

    void readAllFeedsForPull();

    void readFeedsForPull(String category) throws SyncException;
}
