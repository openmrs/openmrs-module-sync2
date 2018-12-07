package org.openmrs.module.sync2.api.scheduler;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.atomfeed.impl.ParentAtomfeedFeedReaderImpl;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncPullTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPullTask.class);

    private ParentFeedReader parentFeedReader;

    /**
     * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
     */
    @Override
    public void execute() {
        parentFeedReader = Context.getRegisteredComponent("sync2.parentFeedReader", ParentAtomfeedFeedReaderImpl.class);

        if (!isExecuting) {
            LOGGER.info("Starting Sync 2.0 Pull Task...");

            startExecuting();
            try {
                parentFeedReader.pullAndProcessAllFeeds();
            }
            catch (Exception e) {
                LOGGER.error("Error while Sync 2.0 Pulling from parent:", e);
            }
            finally {
                stopExecuting();
            }
        }
    }

}
