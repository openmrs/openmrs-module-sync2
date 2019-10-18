package org.openmrs.module.sync2.client.reader.atomfeed.impl;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.openmrs.module.sync2.client.reader.atomfeed.LocalAtomfeedFeedWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("sync2.localFeedReader." + SyncConstants.ATOMFEED_EVENT_HANDLER)
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
	public void readAndPushAllFeeds(SyncCategory category) throws SyncException {
			readAndProcessFeedByCategory(category.getCategory());			
		}	
}
