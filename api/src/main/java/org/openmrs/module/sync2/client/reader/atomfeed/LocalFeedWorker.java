package org.openmrs.module.sync2.client.reader.atomfeed;

import org.ict4h.atomfeed.client.domain.Event;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.client.FeedEventWorker;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;
import org.openmrs.module.sync2.api.service.SyncPushService;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LocalFeedWorker implements FeedEventWorker {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalFeedWorker.class);

	@Override
	public void process(Event event) {
		LOGGER.info("Started local feed event processing (id: {})", event.getId());
		SyncPushService pushService = Context.getRegisteredComponent("sync2.syncPushService", SyncPushService.class);
		List tags = event.getCategories();

		pushService.readAndPushObjectToParent(
				SyncUtils.getValueOfAtomfeedEventTag(tags, AtomfeedTagContent.CATEGORY),
				SyncUtils.getLinks(event.getContent()),
				SyncUtils.getValueOfAtomfeedEventTag(tags, AtomfeedTagContent.EVENT_ACTION)
		);
	}

	@Override
	public void cleanUp(Event event) {
		LOGGER.info("Started local feed cleanUp processing (id: {})", event.getId());
	}
}
