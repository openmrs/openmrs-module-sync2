package org.openmrs.module.sync2.client.reader.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.LocalFeedWorker;
import org.springframework.stereotype.Component;

@Component("sync2.localFeedReader")
public class LocalFeedReaderImpl extends AbstractFeedReader implements LocalFeedReader {

	public LocalFeedReaderImpl() {
		super(new LocalFeedWorker());
	}

	@Override
	protected SyncMethodConfiguration getSyncMethodConf() {
		return configurationService.getSyncConfiguration().getPush();
	}

	@Override
	protected String getBaseUri() {
		return configurationService.getSyncConfiguration().getGeneral().getLocalFeedLocation();
	}

	@Override
	public void readAndPushAllFeeds() {
		readAndProcessAllFeeds();
	}

	@Override
	public void readAndPushAllFeeds(String category) throws SyncException {
		readAndProcessFeedsForCategory(category);
	}
}
