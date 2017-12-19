package org.openmrs.module.sync2.client.reader.impl;

import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientFactory;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.ParentFeedWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.openmrs.module.sync2.api.utils.SyncUtils.readFeedByCategory;

@Service("sync2.parentFeedReader")
public class ParentFeedReaderImpl implements ParentFeedReader {

    private static final String RECENT_FEED = "recent";
    private static final String WS_ATOMFEED = "/ws/atomfeed/";
    private AtomFeedClient atomFeedClient;

    @Autowired
    private SyncConfigurationService configurationService;

    public ParentFeedReaderImpl() {
        this.atomFeedClient = AtomFeedClientFactory.createClient(new ParentFeedWorker());
    }

    public void readAllFeedsForPull() {
        List<ClassConfiguration> pullConf = configurationService.getSyncConfiguration().getPull().getClasses();

        for(ClassConfiguration classConf : pullConf) {
            if(classConf.isEnabled()) {
                readFeedByCategory(classConf.getCategory(), atomFeedClient, configurationService, WS_ATOMFEED);
            }
        }
    }

    private String getParentUri() {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }

    private String getResourceUrlWithCategory(String category) {
        return getParentUri() + WS_ATOMFEED + category + "/" + RECENT_FEED;
    }

}
