package org.openmrs.module.sync2.client.reader;

import org.ict4h.atomfeed.client.domain.Event;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.filter.GenericFeedFilterStrategy;
import org.openmrs.module.atomfeed.api.service.TagService;
import org.openmrs.module.atomfeed.client.FeedEventWorker;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.utils.SyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ParentFeedWorker implements FeedEventWorker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParentFeedWorker.class);

	SyncPullService pullService;

	@Override
	public void process(Event event) {
		LOGGER.info("Started feed event processing (id: {})", event.getId());
		pullService = Context.getRegisteredComponent("sync2.syncPullService", SyncPullService.class);
		List tags = event.getCategories();

		TagService tagService = Context.getRegisteredComponent(SyncConstants.TAG_SERVICE_BEAN, TagService.class);
		List<FeedFilter> feedFilters = tagService.getFeedFiltersFromTags(tags);

		boolean shouldBeSynced = true;
		for (FeedFilter feedFilter : feedFilters) {
			GenericFeedFilterStrategy bean = Context.getRegisteredComponent(feedFilter.getBeanName(), GenericFeedFilterStrategy.class);
			if (!bean.isFilterTagValid(feedFilter.getFilter())) {
				shouldBeSynced = false;
				break;
			}
		}

		if (shouldBeSynced) {
			pullService.pullAndSaveObjectFromParent(
					SyncUtils.getValueOfAtomfeedEventTag(tags, AtomfeedTagContent.CATEGORY),
					SyncUtils.getLinks(event.getContent()),
					SyncUtils.getValueOfAtomfeedEventTag(tags, AtomfeedTagContent.EVENT_ACTION)
			);
		}
	}

	@Override
	public void cleanUp(Event event) {
		LOGGER.info("Started feed cleanUp processing (id: {})", event.getId());
	}
}
