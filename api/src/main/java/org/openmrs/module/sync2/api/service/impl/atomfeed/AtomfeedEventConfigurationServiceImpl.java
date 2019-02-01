package org.openmrs.module.sync2.api.service.impl.atomfeed;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.mapper.EventConfigurationMapper;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.service.EventConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("sync2.eventConfigurationService." + SyncConstants.ATOMFEED_EVENT_HANDLER)
public class AtomfeedEventConfigurationServiceImpl implements EventConfigurationService {

	private static final String OBJECT_UUID_PATTERN = "{uuid}";

	private static final String UUID_PATTERN = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b";

	@Autowired
	@Qualifier("atomfeed.feedConfigurationService")
	private FeedConfigurationService feedConfigurationService;

	@Autowired
	@Qualifier("sync2.eventConfigurationMapper.atomfeed")
	private EventConfigurationMapper<FeedConfiguration> eventConfigurationMapper;

	@Override
	public EventConfiguration getEventConfigurationByCategory(SyncCategory categoryEnum) {
		FeedConfiguration feedConfiguration = feedConfigurationService
				.getFeedConfigurationByCategory(categoryEnum.getCategory());
		return eventConfigurationMapper.map(feedConfiguration);
	}

	@Override
	public String extractUuidFromResourceLinks(Map<String, String> eventResourceLinks, String eventCategory) {
		String uuid;
		uuid = extractBasedOnLinkTemplates(eventResourceLinks, eventCategory);
		if (StringUtils.isBlank(uuid)) {
			uuid = extractBasedOnResourceLinks(eventResourceLinks);
		}
		return uuid;
	}

	private String extractBasedOnLinkTemplates(Map<String, String> eventResourceLinks, String eventCategory) {
		String uuid = null;
		Map<String, String> linkTemplates = feedConfigurationService.getFeedConfigurationByCategory(eventCategory)
				.getLinkTemplates();
		for (String clientTemplate : linkTemplates.keySet()) {
			if (eventResourceLinks.containsKey(clientTemplate)) {
				uuid = extractUuidBasedOnTemplate(eventResourceLinks.get(clientTemplate), linkTemplates.get(clientTemplate));
			}
			if (StringUtils.isNotBlank(uuid)) {
				break;
			}
		}
		return uuid;
	}

	private String extractUuidBasedOnTemplate(String resourceLink, String linkTemplate) {
		String[] linkTemplateSplits = StringUtils.split(linkTemplate, "/");
		int positionOfObjectUuid = getPositionOfUuidPattern(linkTemplateSplits);

		String[] resourceLinkSplits = StringUtils.split(resourceLink, "/");
		String uuid = null;
		if (resourceLinkSplits.length == linkTemplateSplits.length) {
			uuid = resourceLinkSplits[positionOfObjectUuid];
			uuid = getOnlyUuidValue(uuid, linkTemplateSplits[positionOfObjectUuid]);
		}

		return uuid;
	}

	private int getPositionOfUuidPattern(String[] linkTemplateSplits) {
		int positionOfObjectUuid = 0;
		for (positionOfObjectUuid = 0; positionOfObjectUuid < linkTemplateSplits.length; positionOfObjectUuid ++) {
			if (StringUtils.contains(linkTemplateSplits[positionOfObjectUuid], OBJECT_UUID_PATTERN)) {
				break;
			}
		}
		return positionOfObjectUuid;
	}

	private String getOnlyUuidValue(String uuid, String linkTemplateSplit) {
		String result = uuid;
		result = StringUtils.replace(result,StringUtils.substringBefore(linkTemplateSplit, OBJECT_UUID_PATTERN), "");
		result = StringUtils.replace(result,StringUtils.substringAfter(linkTemplateSplit, OBJECT_UUID_PATTERN), "");
		return result;
	}

	private String extractBasedOnResourceLinks(Map<String, String> eventResourceLinks) {
		String uuid = null;
		for (String link : eventResourceLinks.values()) {
			Pattern pattern = Pattern.compile(UUID_PATTERN, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(link);
			while (matcher.find()) {
				uuid = matcher.group();
			}
		}
		return uuid;
	}

}
