package org.openmrs.module.sync2.client.reader.atomfeed.impl;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.atomfeed.ParentAtomfeedFeedWorker;
import org.springframework.stereotype.Service;

@Service("sync2.parentFeedReader." + SyncConstants.ATOMFEED_EVENT_HANDLER)
public class ParentAtomfeedFeedReaderImpl extends AbstractAtomfeedFeedReader implements ParentFeedReader {

	public ParentAtomfeedFeedReaderImpl() {
		super(new ParentAtomfeedFeedWorker());
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
	public void pullAndProcessFeeds(SyncCategory category) throws SyncException {
		readAndProcessFeedByCategory(category.getCategory());
	}
}
