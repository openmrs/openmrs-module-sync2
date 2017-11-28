package org.openmrs.module.sync2.client.reader.impl;

import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.impl.AtomFeedClientImpl;
import org.openmrs.module.sync2.api.SyncConfigurationService;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.SyncFeedWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component("sync2.parentFeedReader")
public class ParentFeedReaderImpl implements ParentFeedReader {
    private static final String WS_ATOMFEED = "/ws/atomfeed/";
    private AtomFeedClient atomFeedClient;
    @Autowired
    private SyncConfigurationService configurationService;


    public ParentFeedReaderImpl() {
        this.atomFeedClient = new AtomFeedClientImpl(new SyncFeedWorker());
    }

    public void readAllFeedsForPull() {
        List<ClassConfiguration> pullConf = configurationService.getSyncConfiguration().getPull().getClasses();

        for(ClassConfiguration classConf : pullConf) {
            if(classConf.isEnabled()) {
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

    private String getParentUri() {
        return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
    }

    private String getResourceUrlWithCategory(String category) {
        //TODO: Start reading from the last page read. Marks table.
        return getParentUri() + WS_ATOMFEED + category + "/" + 1;
    }

}
