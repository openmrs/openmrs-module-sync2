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
import org.openmrs.module.sync2.api.mapper.impl.AtomfeedEventConfigurationMapperImpl;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.configuration.EventConfiguration;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;

import java.util.LinkedHashMap;

public class AtomfeedEventConfigurationServiceImplTest {

	public static final String ADDRESS_UUID = "cd6c9ae2-f689-46f1-abff-7feab13c270E";

	@Spy
	private AtomfeedEventConfigurationMapperImpl eventConfigurationMapper;

	@InjectMocks
	private AtomfeedEventConfigurationServiceImpl atomFeedEventConfigurationService;

	@Mock
	private FeedConfigurationService feedConfigurationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		FeedConfiguration feedConfiguration = new FeedConfiguration();
		feedConfiguration.setLinkTemplates(createPatientLinkTemplates());
		Mockito.when(feedConfigurationService.getFeedConfigurationByCategory(CategoryEnum.PATIENT.getCategory()))
				.thenReturn(feedConfiguration);


	}

	@Test
	public void getEventConfigurationByCategory_shouldReturnCorrectConfiguration() {
		EventConfiguration expected = new EventConfiguration(createPatientLinkTemplates());
		EventConfiguration actual = atomFeedEventConfigurationService.getEventConfigurationByCategory(
				new SyncCategory(CategoryEnum.PATIENT.getCategory(), CategoryEnum.PATIENT.getClazz()));

		Assert.assertNotNull(actual);
		Assert.assertEquals(expected, actual);

	}

	@Test
	public void extractUuidFromResourceLinks_shouldExtractUuidBasedOnLinkTemplate() {
		String category = CategoryEnum.PERSON_ADDRESS.getCategory();
		Mockito.when(feedConfigurationService.getFeedConfigurationByCategory(category))
				.thenReturn(createPersonAddressFeedConfiguration(true));

		String uuid = atomFeedEventConfigurationService.extractUuidFromResourceLinks(
				createResourceLinks(true), category);

		Assert.assertNotNull(uuid);
		Assert.assertEquals(ADDRESS_UUID, uuid);
	}

	@Test
	public void extractUuidFromResourceLinks_shouldExtractUuidBasedOnResourceLink() {
		String category = CategoryEnum.PERSON_ADDRESS.getCategory();
		Mockito.when(feedConfigurationService.getFeedConfigurationByCategory(category))
				.thenReturn(createPersonAddressFeedConfiguration(false));

		String uuid = atomFeedEventConfigurationService.extractUuidFromResourceLinks(
				createResourceLinks(false), category);

		Assert.assertNotNull(uuid);
		Assert.assertEquals(ADDRESS_UUID, uuid);
	}

	private LinkedHashMap<String, String> createResourceLinks(boolean includeRestTemplate) {
		LinkedHashMap<String, String> linkTemplate = new LinkedHashMap<>();
		if (includeRestTemplate) {
			linkTemplate.put("rest", "/ws/rest/v1/person/testParentUuid/address/" + ADDRESS_UUID + "?v=full");
		}
		linkTemplate.put("other", "/ws/other/cd6c9Ae2-f689-46f1-abff-7feab13C2701/address/sasd/" +
				ADDRESS_UUID + "/dsa?dsad/sa");
		return linkTemplate;
	}

	private FeedConfiguration createPersonAddressFeedConfiguration(boolean includeRestTemplate) {
		FeedConfiguration feedConfiguration = new FeedConfiguration();
		feedConfiguration.setLinkTemplates(createPersonAddressLinkTemplates(includeRestTemplate));
		return feedConfiguration;
	}

	private LinkedHashMap<String, String> createPersonAddressLinkTemplates(boolean includeRestTemplate) {
		LinkedHashMap<String, String> linkTemplate = new LinkedHashMap<>();
		if (includeRestTemplate) {
			linkTemplate.put("rest", "/ws/rest/v1/person/{parent-uuid}/address/{uuid}?v=full");
		}
		linkTemplate.put("other", "/ws/other/address/sasd/uuid/dsa?dsad/sa");
		return linkTemplate;
	}

	private LinkedHashMap<String, String> createPatientLinkTemplates() {
		LinkedHashMap<String, String> linkTemplate = new LinkedHashMap<>();
		linkTemplate.put("rest", "/ws/rest/v1/patient/{uuid}?v=full");
		linkTemplate.put("fhir", "/ws/fhir/Patient/{uuid}");
		return linkTemplate;
	}
}
