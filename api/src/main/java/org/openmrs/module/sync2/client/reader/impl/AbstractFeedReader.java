package org.openmrs.module.sync2.client.reader.impl;

import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientFactory;
import org.openmrs.module.atomfeed.client.FeedEventWorker;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFeedReader {

	protected static final String WS_ATOMFEED = "/ws/atomfeed/";

	protected AtomFeedClient atomFeedClient;

	@Autowired
	protected SyncConfigurationService configurationService;

	protected AbstractFeedReader(FeedEventWorker feedEventWorker) {
		this.atomFeedClient = AtomFeedClientFactory.createClient(feedEventWorker);
		AtomfeedUtils.disableMaxFailedEventCondition(atomFeedClient);
	}

	protected void readAndProcessAllFeeds() {
		SyncConfigurationUtils.checkIfConfigurationIsValid();
		readAndProcessFeedsByConfiguration(getSyncMethodConf().getClasses());
	}

	protected void readAndProcessFeedsForCategory(String category) throws SyncException {
		SyncConfigurationUtils.checkIfConfigurationIsValid();
		List<ClassConfiguration> confClasses = getSyncMethodConf().getClasses()
				.stream().filter(conf -> conf.getCategory().equals(category))
				.collect(Collectors.toList());

		if (confClasses.isEmpty()) {
			throw new SyncException("There's no AtomFeed configuration for category " + category);
		}

		readAndProcessFeedsByConfiguration(confClasses);
	}

	protected String getResourceUrlWithCategory(String category) {
		return getBaseUri() + WS_ATOMFEED + category + "/" + SyncConstants.RECENT_FEED;
	}

	protected void readAndProcessFeedByCategory(String category) {
		try {
			URI uri = new URI(getResourceUrlWithCategory(category));
			atomFeedClient.setUri(uri);
			atomFeedClient.process();
		} catch (URISyntaxException e) {
			throw new SyncException("Atomfeed URI is not correct. ", e);
		} catch (Exception e) {
			throw new SyncException(String.format("Error during processing atomfeeds for category %s: ", category), e);
		}
	}

	protected void readAndProcessFeedsByConfiguration(List<ClassConfiguration> conf) {
		for (ClassConfiguration classConf : conf) {
			if (classConf.isEnabled()) {
				readAndProcessFeedByCategory(classConf.getCategory());
			}
		}
	}

	protected abstract SyncMethodConfiguration getSyncMethodConf();

	protected abstract String getBaseUri();
}
