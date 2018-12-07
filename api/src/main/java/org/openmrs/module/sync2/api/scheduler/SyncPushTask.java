package org.openmrs.module.sync2.api.scheduler;

import org.openmrs.module.sync2.api.utils.ContextUtils;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncPushTask extends AbstractTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncPushTask.class);

    /**
     * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
     */
    @Override
    public void execute() {
        LocalFeedReader localFeedReader = ContextUtils.getLocalFeedReader();

        if (!isExecuting) {
            LOGGER.info("Starting Sync 2.0 Push Task...");

            startExecuting();
            try {
                localFeedReader.readAndPushAllFeeds();
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
