package org.openmrs.module.sync2.api.mapper.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.sync2.api.mapper.EventConfigurationMapper;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;

import java.util.LinkedHashMap;

public class EventConfigurationMapperImplTest {

	private FeedConfiguration feedConfiguration;

	private EventConfigurationMapper eventConfigurationMapper;

	@Before
	public void setUp() {
		feedConfiguration = new FeedConfiguration();
		feedConfiguration.setLinkTemplates(createLinkTemplates());

		eventConfigurationMapper = new EventConfigurationMapperImpl();
	}

	@Test
	public void map_shouldReturnCorrectEventConfiguration() {
		EventConfiguration expected = new EventConfiguration(createLinkTemplates());
		EventConfiguration actual = eventConfigurationMapper.map(feedConfiguration);

		Assert.assertNotNull(actual);
		Assert.assertEquals(expected, actual);
	}

	private LinkedHashMap<String, String> createLinkTemplates() {
		LinkedHashMap<String, String> linkTemplate = new LinkedHashMap<>();
		linkTemplate.put("rest", "/ws/rest/v1/patient/{uuid}?v=full");
		linkTemplate.put("fhir", "/ws/fhir/Patient/{uuid}");
		return linkTemplate;
	}

}
