package org.openmrs.module.sync2.client.reader.atomfeed.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.model.enums.CategoryEnum;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.atomfeed.LocalAtomfeedFeedWorker;
import org.springframework.stereotype.Component;

@Component("sync2.localFeedReader")
public class LocalAtomfeedFeedReaderImpl extends AbstractAtomfeedFeedReader implements LocalFeedReader {

	public LocalAtomfeedFeedReaderImpl() {
		super(new LocalAtomfeedFeedWorker());
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
	public void readAndPushAllFeeds(CategoryEnum category) throws SyncException {
		readAndProcessFeedsForCategory(category.getCategory());
	}
}
