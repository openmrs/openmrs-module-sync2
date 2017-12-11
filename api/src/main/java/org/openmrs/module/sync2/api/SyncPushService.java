package org.openmrs.module.sync2.api;

import java.util.Map;

public interface SyncPushService {

    void readDataAndPushToParent(String category, Map<String, String> resourceLinks, String address, String action);
}
