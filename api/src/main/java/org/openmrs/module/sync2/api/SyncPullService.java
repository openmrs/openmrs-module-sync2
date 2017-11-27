package org.openmrs.module.sync2.api;

import java.util.Map;

public interface SyncPullService {

    void pullDataFromParentAndSave(String category, Map<String, String> resourceLinks, String address, String action);
}
