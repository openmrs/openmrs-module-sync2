package org.openmrs.module.sync2.client.reader.impl;

import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.ParentFeedWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.sync2.api.utils.SyncUtils.readFeedByCategory;

@Service("sync2.parentFeedReader")
public class ParentFeedReaderImpl implements ParentFeedReader {
    
    private static final String WS_ATOMFEED = "/ws/atomfeed/";
    private AtomFeedClient atomFeedClient;

    @Autowired
    private SyncConfigurationService configurationService;

    public ParentFeedReaderImpl() {
        this.atomFeedClient = AtomFeedClientFactory.createClient(new ParentFeedWorker());
        AtomfeedUtils.disableMaxFailedEventCondition(atomFeedClient);
    }

    @Override
    public void readAllFeedsForPull() {
        SyncConfigurationUtils.checkIfConfigurationIsValid();
        List<ClassConfiguration> pullConf = configurationService.getSyncConfiguration().getPull().getClasses();

        readFeedsByCategory(pullConf);
    }

    @Override
    public void readFeedsForPull(String category) throws SyncException {
        SyncConfigurationUtils.checkIfConfigurationIsValid();
        List<ClassConfiguration> pullConf = configurationService.getSyncConfiguration().getPull().getClasses()
                .stream().filter(conf -> conf.getCategory().equals(category))
                .collect(Collectors.toList());

        if (pullConf.isEmpty()) {
            throw new SyncException("There's no AtomFeed configuration for category " + category);
        }

        readFeedsByCategory(pullConf);
    }

    private void readFeedsByCategory(List<ClassConfiguration> pullConf) {
        for (ClassConfiguration classConf : pullConf) {
            if (classConf.isEnabled()) {
                readFeedByCategory(classConf.getCategory(), atomFeedClient, configurationService, WS_ATOMFEED);
            }
        }
    }

    private String getParentUri() {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }

    private String getResourceUrlWithCategory(String category) {
        return getParentUri() + WS_ATOMFEED + category + "/" + SyncConstants.RECENT_FEED;
    }

}
