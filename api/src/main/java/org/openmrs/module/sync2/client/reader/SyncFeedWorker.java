package org.openmrs.module.sync2.client.reader;

import org.ict4h.atomfeed.client.domain.Event;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.client.FeedEventWorker;
import org.openmrs.module.sync2.api.SyncPullService;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncFeedWorker implements FeedEventWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFeedWorker.class);

    SyncPullService pullService;

    @Override
    public void process(Event event) {
        LOGGER.info("Started feed event processing (id: {})", event.getId());
        pullService = Context.getRegisteredComponent("sync2.syncPullService", SyncPullService.class);

        //TODO: Extract category and action from event.getCategories().
        pullService.pullDataFromParentAndSave("patient", SyncUtils.getLinks(event.getContent()),
                SyncUtils.getBaseUrl(event.getFeedUri()), "CREATED");
    }

    @Override
    public void cleanUp(Event event) {
        LOGGER.info("Started feed cleanUp processing (id: {})", event.getId());
    }
}