package org.openmrs.module.sync2.client.reader.impl;

import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientFactory;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.service.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.utils.SyncConfigurationUtils;
import org.openmrs.module.sync2.client.reader.LocalFeedWorker;
import org.openmrs.module.sync2.client.reader.LocalFeedReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Component("sync2.localFeedReader")
public class LocalFeedReaderImpl implements LocalFeedReader {
    
    private static final String WS_ATOMFEED = "/ws/atomfeed/";

    private AtomFeedClient atomFeedClient;

    @Autowired
    private SyncConfigurationService configurationService;

    public LocalFeedReaderImpl() {
        this.atomFeedClient = AtomFeedClientFactory.createClient(new LocalFeedWorker());
        AtomfeedUtils.disableMaxFailedEventCondition(atomFeedClient);
    }

    @Override
    public void readAllFeedsForPush() {
        SyncConfigurationUtils.checkIfConfigurationIsValid();
        List<ClassConfiguration> pushConf = configurationService.getSyncConfiguration().getPush().getClasses();

        readFeedsByConfiguration(pushConf);
    }

    @Override
    public void readFeedsForPush(String category) throws SyncException {
        SyncConfigurationUtils.checkIfConfigurationIsValid();
        List<ClassConfiguration> pushConf = configurationService.getSyncConfiguration().getPush().getClasses()
                .stream().filter(conf -> conf.getCategory().equals(category))
                .collect(Collectors.toList());

        if (pushConf.isEmpty()) {
        	throw new SyncException("There's no AtomFeed configuration for category " + category);
        }

        readFeedsByConfiguration(pushConf);
    }

    private void readFeedsByConfiguration(List<ClassConfiguration> conf) {
        for (ClassConfiguration classConf : conf) {
            if (classConf.isEnabled()) {
                readFeedByCategory(classConf.getCategory());
            }
        }
    }

    private void readFeedByCategory(String category) {
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

    private String getResourceUrlWithCategory(String category) {
        String localFeedUri = configurationService.getSyncConfiguration().getGeneral().getLocalFeedLocation();
        return localFeedUri + WS_ATOMFEED + category + "/" + SyncConstants.RECENT_FEED;
    }
}
