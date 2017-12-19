package org.openmrs.module.sync2.api.scheduler;

import org.openmrs.api.context.Context;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.impl.LocalFeedReaderImpl;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncPushTask extends AbstractTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushTask.class);

    private LocalFeedReader localFeedReader;

    /**
     * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
     */
    @Override
    public void execute() {
        localFeedReader = Context.getRegisteredComponent("sync2.localFeedReader", LocalFeedReaderImpl.class);

        if (!isExecuting) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Starting Sync 2.0 Push Task...");
            }

            startExecuting();
            try {
                localFeedReader.readAllFeedsForPush();
            }
            catch (Exception e) {
                LOGGER.error("Error while Sync 2.0 Pushing to the parent:", e);
            }
            finally {
                stopExecuting();
            }
        }
    }

}
