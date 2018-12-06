package org.openmrs.module.sync2.client.reader.atomfeed.impl;

import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.atomfeed.ParentFeedWorker;
import org.springframework.stereotype.Service;

@Service("sync2.parentFeedReader")
public class ParentFeedReaderImpl extends AbstractFeedReader implements ParentFeedReader {

	public ParentFeedReaderImpl() {
		super(new ParentFeedWorker());
	}

	@Override
	protected SyncMethodConfiguration getSyncMethodConf() {
		return configurationService.getSyncConfiguration().getPull();
	}

	@Override
	protected String getBaseUri() {
		return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
	}

	@Override
	public void pullAndProcessAllFeeds() {
		readAndProcessAllFeeds();
	}

	@Override
	public void pullAndProcessFeeds(String category) throws SyncException {
		readAndProcessFeedByCategory(category);
	}
}
