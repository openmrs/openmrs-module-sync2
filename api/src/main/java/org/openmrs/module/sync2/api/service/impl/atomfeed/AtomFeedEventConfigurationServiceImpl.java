package org.openmrs.module.sync2.api.service.impl.atomfeed;

import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.sync2.api.mapper.EventConfigurationMapper;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.api.service.EventConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sync2.atomfeed.AtomFeedEventConfigurationService")
public class AtomFeedEventConfigurationServiceImpl implements EventConfigurationService {

	@Autowired
	private FeedConfigurationService feedConfigurationService;

	@Autowired
	private EventConfigurationMapper eventConfigurationMapper;

	@Override
	public EventConfiguration getEventConfigurationByCategory(CategoryEnum categoryEnum) {
		FeedConfiguration feedConfiguration = feedConfigurationService
				.getFeedConfigurationByCategory(categoryEnum.getCategory());
		return eventConfigurationMapper.map(feedConfiguration);
	}
}
