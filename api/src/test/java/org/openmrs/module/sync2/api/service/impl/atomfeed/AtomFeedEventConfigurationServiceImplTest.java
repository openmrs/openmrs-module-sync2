package org.openmrs.module.sync2.api.service.impl.atomfeed;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.sync2.api.mapper.impl.EventConfigurationMapperImpl;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

import java.util.LinkedHashMap;

public class AtomFeedEventConfigurationServiceImplTest {

	@Spy
	private EventConfigurationMapperImpl eventConfigurationMapper;

	@InjectMocks
	private AtomFeedEventConfigurationServiceImpl atomFeedEventConfigurationService;

	@Mock
	private FeedConfigurationService feedConfigurationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		FeedConfiguration feedConfiguration = new FeedConfiguration();
		feedConfiguration.setLinkTemplates(createLinkTemplates());
		Mockito.when(feedConfigurationService.getFeedConfigurationByCategory(CategoryEnum.PATIENT.getCategory()))
				.thenReturn(feedConfiguration);
	}

	@Test
	public void test() {
		EventConfiguration expected = new EventConfiguration(createLinkTemplates());
		EventConfiguration actual = atomFeedEventConfigurationService.getEventConfigurationByCategory(
				CategoryEnum.PATIENT);

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
