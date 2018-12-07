package org.openmrs.module.sync2.api.service.impl.atomfeed;

import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.mapper.EventConfigurationMapper;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.service.EventConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("sync2.eventConfigurationService." + SyncConstants.ATOMFEED_EVENT_HANDLER)
public class AtomfeedEventConfigurationServiceImpl implements EventConfigurationService {

	@Autowired
	private FeedConfigurationService feedConfigurationService;

	@Autowired
	@Qualifier("sync2.eventConfigurationMapper.atomfeed")
	private EventConfigurationMapper<FeedConfiguration> eventConfigurationMapper;

	@Override
	public EventConfiguration getEventConfigurationByCategory(CategoryEnum categoryEnum) {
		FeedConfiguration feedConfiguration = feedConfigurationService
				.getFeedConfigurationByCategory(categoryEnum.getCategory());
		return eventConfigurationMapper.map(feedConfiguration);
	}
}
